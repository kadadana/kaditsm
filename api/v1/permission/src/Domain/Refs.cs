namespace PermissionService.Domain;

public readonly record struct ObjectRef(string Type, string Id)
{
    public override string ToString() => $"{Type}:{Id}";

    public static ObjectRef Parse(string s)
    {
        var i = s.IndexOf(':');
        if (i <= 0 || i == s.Length - 1)
            throw new ArgumentException($"Invalid object ref: '{s}'");
        return new(s[..i], s[(i + 1)..]);
    }
}
public readonly record struct SubjectRef(ObjectRef Object, string? Relation = null);

public readonly record struct Relationship(ObjectRef Resource, string Relation, SubjectRef Subject);

public enum UpdateOp { Touch, Delete }
public readonly record struct RelationshipUpdate(UpdateOp Op, Relationship Relationship);