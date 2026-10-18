using Grpc.Core;
using PermissionService.Api.Grpc.V1;
using PermissionService.Application;
using PermissionService.Domain;

namespace PermissionService.Api.Grpc;

public sealed class PermissionCheckService(ICheckPermission check)
    : PermissionCheck.PermissionCheckBase
{
    public override async Task<CheckResponse> Check(CheckRequest r, ServerCallContext ctx)
    {
        var query = new CheckPermissionQuery(
            Subject: ToSubject(r.Subject),
            Permission: r.Permission,
            Resource: ToObject(r.Resource),
            ConsistencyToken: string.IsNullOrEmpty(r.ConsistencyToken) ? null : r.ConsistencyToken);

        var allowed = await check.ExecuteAsync(query, ctx.CancellationToken);
        return new CheckResponse { Allowed = allowed };
    }

    private static ObjectRef ToObject(ObjectReference? o) =>
        o is null || string.IsNullOrWhiteSpace(o.Type) || string.IsNullOrWhiteSpace(o.Id)
            ? throw new ArgumentException("type and id are required.")
            : new ObjectRef(o.Type, o.Id);

    private static SubjectRef ToSubject(SubjectReference? s) =>
        s is null
            ? throw new ArgumentException("subject is required.")
            : new SubjectRef(ToObject(s.Object), string.IsNullOrEmpty(s.Relation) ? null : s.Relation);
}