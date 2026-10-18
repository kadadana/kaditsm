using PermissionService.Domain;
namespace PermissionService.Application;

public interface IPermissionStore
{
    Task<bool> CheckAsync(ObjectRef resource, string permission, SubjectRef subject,
                          string? consistencyToken, CancellationToken ct);

    Task<string> WriteAsync(IReadOnlyList<RelationshipUpdate> updates, CancellationToken ct);
}

public interface ICheckPermission
{
    Task<bool> ExecuteAsync(CheckPermissionQuery query, CancellationToken ct);
}

public sealed record CheckPermissionQuery(
    SubjectRef Subject, string Permission, ObjectRef Resource, string? ConsistencyToken = null);