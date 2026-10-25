using PermissionService.Domain;
namespace PermissionService.Application;

public interface IRoleRepository
{
    Task<Role?> GetAsync(Guid id, CancellationToken ct);
    Task AddAsync(Role role, CancellationToken ct);
    Task<RoleBinding?> GetBindingAsync(Guid id, CancellationToken ct);
    Task<IReadOnlyList<RoleBinding>> BindingsOfAsync(Guid roleId, CancellationToken ct);
    Task AddBindingAsync(RoleBinding binding, CancellationToken ct);
}

public interface IAuditLog
{
    void Record(string actor, string action, string target, object details);
}

public interface IUnitOfWork
{
    Task CommitAsync(CancellationToken ct);
}

public sealed class NotFoundException(string message) : Exception(message);

public sealed class RoleAdministration(
    IRoleRepository roles, IAuditLog audit, IUnitOfWork uow, IPermissionStore store)
{
    private static readonly ObjectRef Platform = new("platform", "main");

    public async Task<Role> CreateRoleAsync(SubjectRef actor, string name, IEnumerable<string> actions, CancellationToken ct)
    {
        await RequirePlatformAdminAsync(actor, ct);

        var role = Role.Create(name, actions);
        await roles.AddAsync(role, ct);
        audit.Record(Who(actor), "role.create", $"role-def:{role.Id}", new { role.Name, role.Actions });
        await uow.CommitAsync(ct);
        return role;
    }

    public async Task<(RoleBinding Binding, string Token)> BindRoleAsync(
        SubjectRef actor, Guid roleId, ObjectRef scope, CancellationToken ct)
    {
        var role = await roles.GetAsync(roleId, ct) ?? throw new NotFoundException($"Role {roleId} not found.");
        await RequireCanGrantAsync(actor, scope, role.Actions, ct);

        var invalid = role.Actions.Where(a => !ActionCatalog.IsValid(scope.Type, a)).ToList();
        if (invalid.Count > 0)
            throw new ArgumentException($"Actions not valid on '{scope.Type}': {string.Join(", ", invalid)}");

        var binding = RoleBinding.Create(roleId, scope);
        await roles.AddBindingAsync(binding, ct);
        audit.Record(Who(actor), "binding.create", scope.ToString(), new { binding.Id, roleId, role.Actions });
        await uow.CommitAsync(ct);

        var token = (await WriteChunked(
            BindingExpansion.Expand(binding, role.Actions).Select(Touch), ct))!;
        return (binding, token);
    }

    public async Task<string> AssignAsync(SubjectRef actor, Guid bindingId, SubjectRef subject, CancellationToken ct)
    {
        var binding = await roles.GetBindingAsync(bindingId, ct)
                      ?? throw new NotFoundException($"Binding {bindingId} not found.");
        var role = await roles.GetAsync(binding.RoleId, ct)
                   ?? throw new NotFoundException($"Role {binding.RoleId} not found.");
        await RequireCanGrantAsync(actor, binding.Scope, role.Actions, ct);

        audit.Record(Who(actor), "assignment.create", binding.Scope.ToString(),
            new
            {
                bindingId,
                subject = subject.Relation is null
                ? subject.Object.ToString()
                : $"{subject.Object}#{subject.Relation}"
            });
        await uow.CommitAsync(ct);

        return (await WriteChunked(
            [Touch(new Relationship(BindingExpansion.RoleObject(bindingId), "member", subject))], ct))!;
    }

    public async Task<string?> UpdateRoleActionsAsync(
        SubjectRef actor, Guid roleId, IEnumerable<string> newActions, CancellationToken ct)
    {
        await RequirePlatformAdminAsync(actor, ct);

        var role = await roles.GetAsync(roleId, ct) ?? throw new NotFoundException($"Role {roleId} not found.");
        var bindings = await roles.BindingsOfAsync(roleId, ct);

        var next = Role.Normalize(newActions);
        var added = next.Except(role.Actions).ToList();
        var removed = role.Actions.Except(next).ToList();

        foreach (var b in bindings)
            foreach (var a in added)
                if (!ActionCatalog.IsValid(b.ScopeType, a))
                    throw new ArgumentException($"'{a}' is not valid on '{b.ScopeType}' (binding {b.Id}).");

        string? token = null;

        if (removed.Count > 0)
            token = await WriteChunked(bindings.SelectMany(b => BindingExpansion.Expand(b, removed)).Select(Delete), ct);

        role.SetActions(next);
        audit.Record(Who(actor), "role.update_actions", $"role-def:{roleId}", new { added, removed, bindings = bindings.Count });
        await uow.CommitAsync(ct);

        if (added.Count > 0)
            token = await WriteChunked(bindings.SelectMany(b => BindingExpansion.Expand(b, added)).Select(Touch), ct);

        return token;
    }

    private Task<bool> IsPlatformAdminAsync(SubjectRef actor, CancellationToken ct) =>
        store.CheckAsync(Platform, "administer", actor, ConsistencyToken.Full, ct);

    private async Task RequirePlatformAdminAsync(SubjectRef actor, CancellationToken ct)
    {
        if (!await IsPlatformAdminAsync(actor, ct))
            throw new ForbiddenException("Platform admin required.");
    }

    private async Task RequireCanGrantAsync(
        SubjectRef actor, ObjectRef scope, IEnumerable<string> actions, CancellationToken ct)
    {
        if (!ActionCatalog.IsKnownType(scope.Type))
            throw new ArgumentException($"Unknown scope type '{scope.Type}'.");

        if (await IsPlatformAdminAsync(actor, ct)) return;

        if (!await store.CheckAsync(scope, "manage_members", actor, ConsistencyToken.Full, ct))
            throw new ForbiddenException($"You cannot manage access on {scope}.");

        foreach (var a in actions)
            if (!await store.CheckAsync(scope, a, actor, ConsistencyToken.Full, ct))
                throw new ForbiddenException($"You cannot grant '{a}' on {scope} because you do not have it.");
    }

    private static string Who(SubjectRef actor) => actor.Object.ToString();

    private async Task<string?> WriteChunked(IEnumerable<RelationshipUpdate> updates, CancellationToken ct)
    {
        string? token = null;
        foreach (var chunk in updates.Chunk(500))
            token = await store.WriteAsync(chunk, ct);
        return token;
    }

    private static RelationshipUpdate Touch(Relationship r) => new(UpdateOp.Touch, r);
    private static RelationshipUpdate Delete(Relationship r) => new(UpdateOp.Delete, r);
}