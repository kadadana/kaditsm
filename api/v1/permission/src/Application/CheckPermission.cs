namespace PermissionService.Application;

public sealed class CheckPermission(IPermissionStore store) : ICheckPermission
{
    public Task<bool> ExecuteAsync(CheckPermissionQuery q, CancellationToken ct)
    {
        if (string.IsNullOrWhiteSpace(q.Permission))
            throw new ArgumentException("Permission is required.");

        return store.CheckAsync(q.Resource, q.Permission, q.Subject, q.ConsistencyToken, ct);
    }
}