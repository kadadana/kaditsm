using PermissionService.Application;
using PermissionService.Domain;

namespace PermissionService.Host.Dev;

internal sealed class DenyAllStore : IPermissionStore
{
    public Task<bool> CheckAsync(ObjectRef resource, string permission, SubjectRef subject,
                                 string? consistencyToken, CancellationToken ct)
        => Task.FromResult(false);

    public Task<string> WriteAsync(IReadOnlyList<RelationshipUpdate> updates, CancellationToken ct)
        => Task.FromResult("dev-token");
}