using DotNetEnv;
using Microsoft.Extensions.Options;
using PermissionService.Adapters.SpiceDb;
using PermissionService.Domain;

namespace PermissionService.IntegrationTests;

public class SpiceDbStoreTests
{
    internal static SpiceDbStore CreateStore()
    {
        Env.NoClobber().TraversePath().Load();
        return new SpiceDbStore(Options.Create(new SpiceDbOptions
        {
            Endpoint = Environment.GetEnvironmentVariable("SpiceDb__Endpoint")
                       ?? throw new InvalidOperationException("SpiceDb__Endpoint missing"),
            Token = Environment.GetEnvironmentVariable("SpiceDb__Token")
                    ?? throw new InvalidOperationException("SpiceDb__Token missing"),
        }));
    }

    private static RelationshipUpdate Touch(Relationship r) => new(UpdateOp.Touch, r);
    private static RelationshipUpdate Delete(Relationship r) => new(UpdateOp.Delete, r);

    [Fact]
    public async Task Role_bound_at_project_grants_view_on_child_ticket_but_not_edit()
    {
        using var store = CreateStore();
        var id = Guid.NewGuid().ToString("N");
        var user = new SubjectRef(new ObjectRef("user", $"u{id}"));
        var role = new ObjectRef("role", $"r{id}");
        var project = new ObjectRef("project", $"p{id}");
        var ticket = new ObjectRef("ticket", $"t{id}");

        var token = await store.WriteAsync(
        [
            Touch(new(role, "member", user)),
            Touch(new(project, "view_granted", new SubjectRef(role, "member"))),
            Touch(new(ticket, "parent", new SubjectRef(project))),
        ], default);

        Assert.True(await store.CheckAsync(ticket, "view", user, token, default));
        Assert.False(await store.CheckAsync(ticket, "edit", user, token, default));
    }

    [Fact]
    public async Task Direct_action_grant_works_without_any_role_and_stays_scoped()
    {
        using var store = CreateStore();
        var id = Guid.NewGuid().ToString("N");
        var user = new SubjectRef(new ObjectRef("user", $"u{id}"));
        var project = new ObjectRef("project", $"p{id}");
        var t1 = new ObjectRef("ticket", $"t1{id}");
        var t2 = new ObjectRef("ticket", $"t2{id}");

        var token = await store.WriteAsync(
        [
            Touch(new(t1, "parent", new SubjectRef(project))),
            Touch(new(t2, "parent", new SubjectRef(project))),
            Touch(new(t1, "edit_granted", user)),
        ], default);

        Assert.True(await store.CheckAsync(t1, "edit", user, token, default));
        Assert.False(await store.CheckAsync(t2, "edit", user, token, default));
    }

    [Fact]
    public async Task Removing_role_membership_revokes_access()
    {
        using var store = CreateStore();
        var id = Guid.NewGuid().ToString("N");
        var user = new SubjectRef(new ObjectRef("user", $"u{id}"));
        var role = new ObjectRef("role", $"r{id}");
        var project = new ObjectRef("project", $"p{id}");
        var membership = new Relationship(role, "member", user);

        var t1 = await store.WriteAsync(
        [
            Touch(membership),
            Touch(new(project, "view_granted", new SubjectRef(role, "member"))),
        ], default);
        Assert.True(await store.CheckAsync(project, "view", user, t1, default));

        var t2 = await store.WriteAsync([Delete(membership)], default);
        Assert.False(await store.CheckAsync(project, "view", user, t2, default));
    }
}