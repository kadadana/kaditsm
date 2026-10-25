using PermissionService.Domain;

namespace PermissionService.IntegrationTests;

public class BindingExpansionTests
{
    [Fact]
    public void Expands_one_tuple_per_action_pointing_to_binding_members()
    {
        var b = RoleBinding.Create(Guid.NewGuid(), new ObjectRef("project", "42"));

        var tuples = BindingExpansion.Expand(b, ["view", "edit"]).ToList();

        Assert.Equal(2, tuples.Count);
        Assert.All(tuples, t =>
        {
            Assert.Equal(b.Scope, t.Resource);
            Assert.Equal(new SubjectRef(BindingExpansion.RoleObject(b.Id), "member"), t.Subject);
        });
        Assert.Contains(tuples, t => t.Relation == "view_granted");
        Assert.Contains(tuples, t => t.Relation == "edit_granted");
    }
}