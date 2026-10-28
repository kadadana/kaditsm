using PermissionService.Domain;
namespace PermissionService.Application;

public interface IPermissionStore
{
    Task<bool> CheckAsync(ObjectRef resource, string permission, SubjectRef subject,
                          string? consistencyToken, CancellationToken ct);

    Task<string> WriteAsync(IReadOnlyList<RelationshipUpdate> updates, CancellationToken ct);

    Task<IReadOnlyList<SubjectRef>> ReadSubjectsAsync(
        ObjectRef resource, string relation, string? consistencyToken, CancellationToken ct);

    Task<string> DeleteRelationshipsAsync(ObjectRef resource, string relation, CancellationToken ct);
}
public interface ICheckPermission
{
    Task<bool> ExecuteAsync(CheckPermissionQuery query, CancellationToken ct);
}

public sealed record CheckPermissionQuery(
    SubjectRef Subject, string Permission, ObjectRef Resource, string? ConsistencyToken = null);

public static class ConsistencyToken
{
    public const string Full = "@full";
}