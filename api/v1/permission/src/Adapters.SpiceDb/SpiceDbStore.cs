using Authzed.Api.V1;
using Grpc.Core;
using Grpc.Net.Client;
using Microsoft.Extensions.Options;
using PermissionService.Application;
using PermissionService.Domain;
using Spice = Authzed.Api.V1;
using DomainUpdate = PermissionService.Domain.RelationshipUpdate;

namespace PermissionService.Adapters.SpiceDb;

public sealed class SpiceDbOptions
{
    public string Endpoint { get; set; } = "";
    public string Token { get; set; } = "";
}

public sealed class SpiceDbStore : IPermissionStore, IDisposable
{
    private readonly GrpcChannel _channel;
    private readonly PermissionsService.PermissionsServiceClient _client;
    private readonly Metadata _headers;

    public SpiceDbStore(IOptions<SpiceDbOptions> options)
    {
        var o = options.Value;
        if (string.IsNullOrWhiteSpace(o.Endpoint) || string.IsNullOrWhiteSpace(o.Token))
            throw new InvalidOperationException("SpiceDb:Endpoint and SpiceDb:Token are required.");

        _channel = GrpcChannel.ForAddress($"http://{o.Endpoint}");
        _client = new PermissionsService.PermissionsServiceClient(_channel);
        _headers = new Metadata { { "authorization", $"Bearer {o.Token}" } };
    }

    public async Task<bool> CheckAsync(ObjectRef resource, string permission, SubjectRef subject,
                                       string? consistencyToken, CancellationToken ct)
    {

        var res = await Call(() => _client.CheckPermissionAsync(new CheckPermissionRequest
        {
            Consistency = ToConsistency(consistencyToken),
            Resource = ToObject(resource),
            Permission = permission,
            Subject = ToSubject(subject),
        },
        headers: _headers, cancellationToken: ct).ResponseAsync);

        return res.Permissionship == CheckPermissionResponse.Types.Permissionship.HasPermission;
    }

    public async Task<string> WriteAsync(IReadOnlyList<DomainUpdate> updates, CancellationToken ct)
    {
        var req = new WriteRelationshipsRequest();
        foreach (var u in updates)
        {
            req.Updates.Add(new Spice.RelationshipUpdate
            {
                Operation = u.Op == UpdateOp.Touch
                    ? Spice.RelationshipUpdate.Types.Operation.Touch
                    : Spice.RelationshipUpdate.Types.Operation.Delete,
                Relationship = new Spice.Relationship
                {
                    Resource = ToObject(u.Relationship.Resource),
                    Relation = u.Relationship.Relation,
                    Subject = ToSubject(u.Relationship.Subject),
                },
            });
        }

        var res = await Call(() => _client.WriteRelationshipsAsync(req,
            headers: _headers, cancellationToken: ct).ResponseAsync);
        return res.WrittenAt.Token;   // ZedToken
    }

    private static Consistency ToConsistency(string? token) => token switch
    {
        null => new Consistency { MinimizeLatency = true },
        ConsistencyToken.Full => new Consistency { FullyConsistent = true },
        _ => new Consistency { AtLeastAsFresh = new ZedToken { Token = token } },
    };

    private static ObjectReference ToObject(ObjectRef o) =>
        new() { ObjectType = o.Type, ObjectId = o.Id };

    private static SubjectReference ToSubject(SubjectRef s) =>
        new() { Object = ToObject(s.Object), OptionalRelation = s.Relation ?? "" };

    public void Dispose() => _channel.Dispose();
    private static async Task<T> Call<T>(Func<Task<T>> call)
    {
        try { return await call(); }
        catch (RpcException e) when (e.StatusCode is StatusCode.Unavailable or StatusCode.DeadlineExceeded)
        {
            throw new PermissionStoreUnavailableException("SpiceDB is unavailable.", e);
        }
        catch (RpcException e) when (e.StatusCode == StatusCode.InvalidArgument)
        {
            throw new ArgumentException("Invalid object or subject reference.");
        }
    }

    public async Task<IReadOnlyList<SubjectRef>> ReadSubjectsAsync(
    ObjectRef resource, string relation, string? token, CancellationToken ct)
    {
        try
        {
            using var call = _client.ReadRelationships(new ReadRelationshipsRequest
            {
                Consistency = ToConsistency(token),
                RelationshipFilter = new RelationshipFilter
                {
                    ResourceType = resource.Type,
                    OptionalResourceId = resource.Id,
                    OptionalRelation = relation,
                },
                OptionalLimit = 1000,
            }, headers: _headers, cancellationToken: ct);

            var list = new List<SubjectRef>();
            while (await call.ResponseStream.MoveNext(ct))
            {
                var s = call.ResponseStream.Current.Relationship.Subject;
                list.Add(new SubjectRef(new ObjectRef(s.Object.ObjectType, s.Object.ObjectId),
                    string.IsNullOrEmpty(s.OptionalRelation) ? null : s.OptionalRelation));
            }
            return list;
        }
        catch (RpcException e) when (e.StatusCode is StatusCode.Unavailable or StatusCode.DeadlineExceeded)
        {
            throw new PermissionStoreUnavailableException("SpiceDB is unavailable.", e);
        }
    }

    public async Task<string> DeleteRelationshipsAsync(ObjectRef resource, string relation, CancellationToken ct)
    {
        var res = await Call(() => _client.DeleteRelationshipsAsync(new DeleteRelationshipsRequest
        {
            RelationshipFilter = new RelationshipFilter
            {
                ResourceType = resource.Type,
                OptionalResourceId = resource.Id,
                OptionalRelation = relation,
            },
        }, headers: _headers, cancellationToken: ct).ResponseAsync);
        return res.DeletedAt.Token;
    }
}