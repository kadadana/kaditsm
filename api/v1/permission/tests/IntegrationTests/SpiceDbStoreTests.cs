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
            Endpoint = Environment.GetEnvironmentVariable("SPICEDB__ENDPOINT")
                       ?? throw new InvalidOperationException("SPICEDB__ENDPOINT missing"),
            Token = Environment.GetEnvironmentVariable("SPICEDB__TOKEN")
                    ?? throw new InvalidOperationException("SPICEDB__TOKEN missing"),
        }));
        
    }

    private static RelationshipUpdate Touch(Relationship r) => new(UpdateOp.Touch, r);
    private static RelationshipUpdate Delete(Relationship r) => new(UpdateOp.Delete, r);

    [Fact]
    public async Task Group_member_can_view_ticket_through_project_but_cannot_edit()
    {
        using var store = CreateStore();
        var id = Guid.NewGuid().ToString("N");

        var user = new SubjectRef(new ObjectRef("user", $"u{id}"));
        var group = new ObjectRef("group", $"g{id}");
        var project = new ObjectRef("project", $"p{id}");
        var ticket = new ObjectRef("ticket", $"t{id}");

        var token = await store.WriteAsync(
        [
            Touch(new(group, "member", user)),
            Touch(new(project, "member", new SubjectRef(group, "member"))),
            Touch(new(ticket, "parent", new SubjectRef(project))),
        ], default);

        Assert.True(await store.CheckAsync(ticket, "view", user, token, default));
        Assert.False(await store.CheckAsync(ticket, "edit", user, token, default));
    }

    [Fact]
    public async Task Removing_group_membership_revokes_access()
    {
        using var store = CreateStore();
        var id = Guid.NewGuid().ToString("N");

        var user = new SubjectRef(new ObjectRef("user", $"u{id}"));
        var group = new ObjectRef("group", $"g{id}");
        var project = new ObjectRef("project", $"p{id}");
        var membership = new Relationship(group, "member", user);

        var t1 = await store.WriteAsync(
        [
            Touch(membership),
            Touch(new(project, "member", new SubjectRef(group, "member"))),
        ], default);
        Assert.True(await store.CheckAsync(project, "view", user, t1, default));

        var t2 = await store.WriteAsync([Delete(membership)], default);
        Assert.False(await store.CheckAsync(project, "view", user, t2, default));
    }
}