namespace PermissionService.Domain;

public static class ActionCatalog
{
    public static bool IsKnownType(string type) => ByType.ContainsKey(type);
    private static readonly Dictionary<string, HashSet<string>> ByType = new()
    {
        ["project"] = ["view", "edit", "manage_members"],
        ["ticket"] = ["view", "edit"],
    };

    public static bool IsKnown(string action) => ByType.Values.Any(s => s.Contains(action));
    public static bool IsValid(string type, string action) =>
        ByType.TryGetValue(type, out var s) && s.Contains(action);

    public static IReadOnlyDictionary<string, string[]> Snapshot() =>
ByType.ToDictionary(k => k.Key, v => v.Value.Order().ToArray());
}

public sealed class Role
{
    private Role() { }
    public Guid Id { get; private set; }
    public string Name { get; private set; } = "";
    public List<string> Actions { get; private set; } = [];

    public static Role Create(string name, IEnumerable<string> actions)
    {
        if (string.IsNullOrWhiteSpace(name)) throw new ArgumentException("Role name is required.");
        return new() { Id = Guid.CreateVersion7(), Name = name.Trim(), Actions = Normalize(actions) };
    }

    public void SetActions(IEnumerable<string> actions) => Actions = Normalize(actions);

    public static List<string> Normalize(IEnumerable<string> actions)
    {
        var list = actions.Select(a => a.Trim()).Where(a => a.Length > 0).Distinct().Order().ToList();
        if (list.Count == 0) throw new ArgumentException("A role needs at least one action.");
        var unknown = list.Where(a => !ActionCatalog.IsKnown(a)).ToList();
        if (unknown.Count > 0) throw new ArgumentException($"Unknown actions: {string.Join(", ", unknown)}");
        return list;
    }
}

public sealed class RoleBinding
{
    private RoleBinding() { }
    public Guid Id { get; private set; }
    public Guid RoleId { get; private set; }
    public string ScopeType { get; private set; } = "";
    public string ScopeId { get; private set; } = "";
    public ObjectRef Scope => new(ScopeType, ScopeId);

    public static RoleBinding Create(Guid roleId, ObjectRef scope) =>
        new() { Id = Guid.CreateVersion7(), RoleId = roleId, ScopeType = scope.Type, ScopeId = scope.Id };
}

public sealed record AuditEntry(
    Guid Id, DateTimeOffset At, string Actor, string Action, string Target, string DetailsJson);

public static class BindingExpansion
{
    public static ObjectRef RoleObject(Guid bindingId) => new("role", bindingId.ToString("N"));

    public static IEnumerable<Relationship> Expand(RoleBinding b, IEnumerable<string> actions) =>
        actions.Select(a => new Relationship(
            b.Scope, $"{a}_granted", new SubjectRef(RoleObject(b.Id), "member")));
}