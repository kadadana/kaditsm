using DotNetEnv;
using Microsoft.Extensions.DependencyInjection;
using PermissionService.Adapters.Postgres;
using PermissionService.Adapters.SpiceDb;
using PermissionService.Application;
using PermissionService.Domain;

namespace PermissionService.IntegrationTests;

public class RoleAdministrationTests
{
    private static ServiceProvider Build()
    {
        Env.NoClobber().TraversePath().Load();
        var s = new ServiceCollection();
        s.Configure<SpiceDbOptions>(o =>
        {
            o.Endpoint = Environment.GetEnvironmentVariable("SpiceDb__Endpoint")!;
            o.Token = Environment.GetEnvironmentVariable("SpiceDb__Token")!;
        });
        s.AddSingleton<IPermissionStore, SpiceDbStore>();
        s.AddPostgresAdapter(Environment.GetEnvironmentVariable("ConnectionStrings__Permission")!);
        s.AddScoped<RoleAdministration>();
        return s.BuildServiceProvider();
    }

    private static SubjectRef NewUser(string prefix) =>
        new(new ObjectRef("user", $"{prefix}{Guid.NewGuid():N}"));

    private static RelationshipUpdate Touch(ObjectRef res, string rel, SubjectRef subj) =>
        new(UpdateOp.Touch, new Relationship(res, rel, subj));

    private static async Task<SubjectRef> NewPlatformAdmin(IPermissionStore store)
    {
        var admin = NewUser("padmin");
        await store.WriteAsync([Touch(new ObjectRef("platform", "main"), "admin", admin)], default);
        return admin;
    }

    [Fact]
    public async Task Changing_role_actions_propagates_to_existing_bindings()
    {
        await using var sp = Build();
        using var scope = sp.CreateScope();
        var admin = scope.ServiceProvider.GetRequiredService<RoleAdministration>();
        var store = scope.ServiceProvider.GetRequiredService<IPermissionStore>();
        var actor = await NewPlatformAdmin(store);

        var id = Guid.NewGuid().ToString("N");
        var project = new ObjectRef("project", $"p{id}");
        var user = NewUser("u");

        var role = await admin.CreateRoleAsync(actor, $"reviewer-{id}", ["view"], default);
        var (binding, _) = await admin.BindRoleAsync(actor, role.Id, project, default);
        var t1 = await admin.AssignAsync(actor, binding.Id, user, default);

        Assert.True(await store.CheckAsync(project, "view", user, t1, default));
        Assert.False(await store.CheckAsync(project, "edit", user, t1, default));

        var t2 = await admin.UpdateRoleActionsAsync(actor, role.Id, ["view", "edit"], default);
        Assert.True(await store.CheckAsync(project, "edit", user, t2, default));

        var t3 = await admin.UpdateRoleActionsAsync(actor, role.Id, ["edit"], default);
        Assert.False(await store.CheckAsync(project, "view", user, t3, default));
        Assert.True(await store.CheckAsync(project, "edit", user, t3, default));
    }

    [Fact]
    public async Task Binding_rejects_actions_that_do_not_exist_on_scope_type()
    {
        await using var sp = Build();
        using var scope = sp.CreateScope();
        var admin = scope.ServiceProvider.GetRequiredService<RoleAdministration>();
        var store = scope.ServiceProvider.GetRequiredService<IPermissionStore>();
        var actor = await NewPlatformAdmin(store);
        var id = Guid.NewGuid().ToString("N");

        var role = await admin.CreateRoleAsync(actor, $"mgr-{id}", ["view", "manage_members"], default);

        await Assert.ThrowsAsync<ArgumentException>(() =>
            admin.BindRoleAsync(actor, role.Id, new ObjectRef("ticket", $"t{id}"), default));
    }

    [Fact]
    public async Task Non_admin_cannot_create_roles()
    {
        await using var sp = Build();
        using var scope = sp.CreateScope();
        var admin = scope.ServiceProvider.GetRequiredService<RoleAdministration>();

        await Assert.ThrowsAsync<ForbiddenException>(() =>
            admin.CreateRoleAsync(NewUser("nobody"), $"x-{Guid.NewGuid():N}", ["view"], default));
    }

    [Fact]
    public async Task Project_manager_cannot_grant_actions_they_do_not_have()
    {
        await using var sp = Build();
        using var scope = sp.CreateScope();
        var admin = scope.ServiceProvider.GetRequiredService<RoleAdministration>();
        var store = scope.ServiceProvider.GetRequiredService<IPermissionStore>();
        var platformAdmin = await NewPlatformAdmin(store);

        var id = Guid.NewGuid().ToString("N");
        var project = new ObjectRef("project", $"p{id}");
        var viewRole = await admin.CreateRoleAsync(platformAdmin, $"v-{id}", ["view"], default);
        var editRole = await admin.CreateRoleAsync(platformAdmin, $"e-{id}", ["edit"], default);

        var manager = NewUser("mgr");
        await store.WriteAsync(
        [
            Touch(project, "manage_members_granted", manager),
            Touch(project, "view_granted", manager),
        ], default);

        var (binding, _) = await admin.BindRoleAsync(manager, viewRole.Id, project, default);
        await admin.AssignAsync(manager, binding.Id, NewUser("u"), default);

        await Assert.ThrowsAsync<ForbiddenException>(() =>
            admin.BindRoleAsync(manager, editRole.Id, project, default));
    }

    [Fact]
    public async Task User_without_manage_members_cannot_assign()
    {
        await using var sp = Build();
        using var scope = sp.CreateScope();
        var admin = scope.ServiceProvider.GetRequiredService<RoleAdministration>();
        var store = scope.ServiceProvider.GetRequiredService<IPermissionStore>();
        var platformAdmin = await NewPlatformAdmin(store);

        var id = Guid.NewGuid().ToString("N");
        var role = await admin.CreateRoleAsync(platformAdmin, $"r-{id}", ["view"], default);
        var (binding, _) = await admin.BindRoleAsync(platformAdmin, role.Id,
            new ObjectRef("project", $"p{id}"), default);

        await Assert.ThrowsAsync<ForbiddenException>(() =>
            admin.AssignAsync(NewUser("stranger"), binding.Id, NewUser("u"), default));
    }

    [Fact]
    public async Task Unassign_revokes_access_and_access_list_reflects_it()
    {
        await using var sp = Build();
        using var scope = sp.CreateScope();
        var admin = scope.ServiceProvider.GetRequiredService<RoleAdministration>();
        var store = scope.ServiceProvider.GetRequiredService<IPermissionStore>();
        var actor = await NewPlatformAdmin(store);

        var id = Guid.NewGuid().ToString("N");
        var project = new ObjectRef("project", $"p{id}");
        var user = NewUser("u");

        var role = await admin.CreateRoleAsync(actor, $"r-{id}", ["view"], default);
        var (binding, _) = await admin.BindRoleAsync(actor, role.Id, project, default);
        await admin.AssignAsync(actor, binding.Id, user, default);

        var entry = Assert.Single(await admin.GetAccessAsync(actor, project, default));
        Assert.Contains(entry.Members, m => m.Object == user.Object);

        var t = await admin.UnassignAsync(actor, binding.Id, user, default);
        Assert.False(await store.CheckAsync(project, "view", user, t, default));
        Assert.Empty(Assert.Single(await admin.GetAccessAsync(actor, project, default)).Members);
    }

    [Fact]
    public async Task Unbind_removes_access_and_unblocks_role_deletion()
    {
        await using var sp = Build();
        using var scope = sp.CreateScope();
        var admin = scope.ServiceProvider.GetRequiredService<RoleAdministration>();
        var store = scope.ServiceProvider.GetRequiredService<IPermissionStore>();
        var actor = await NewPlatformAdmin(store);

        var id = Guid.NewGuid().ToString("N");
        var project = new ObjectRef("project", $"p{id}");
        var user = NewUser("u");

        var role = await admin.CreateRoleAsync(actor, $"r-{id}", ["view"], default);
        var (binding, _) = await admin.BindRoleAsync(actor, role.Id, project, default);
        await admin.AssignAsync(actor, binding.Id, user, default);

        await Assert.ThrowsAsync<ConflictException>(() => admin.DeleteRoleAsync(actor, role.Id, default));

        var t = await admin.UnbindAsync(actor, binding.Id, default);
        Assert.False(await store.CheckAsync(project, "view", user, t, default));
        Assert.Empty(await admin.GetAccessAsync(actor, project, default));

        await admin.DeleteRoleAsync(actor, role.Id, default);
    }

    [Fact]
    public async Task Stranger_cannot_read_access_list_or_unassign()
    {
        await using var sp = Build();
        using var scope = sp.CreateScope();
        var admin = scope.ServiceProvider.GetRequiredService<RoleAdministration>();
        var store = scope.ServiceProvider.GetRequiredService<IPermissionStore>();
        var actor = await NewPlatformAdmin(store);

        var id = Guid.NewGuid().ToString("N");
        var project = new ObjectRef("project", $"p{id}");
        var role = await admin.CreateRoleAsync(actor, $"r-{id}", ["view"], default);
        var (binding, _) = await admin.BindRoleAsync(actor, role.Id, project, default);
        var stranger = NewUser("stranger");

        await Assert.ThrowsAsync<ForbiddenException>(() => admin.GetAccessAsync(stranger, project, default));
        await Assert.ThrowsAsync<ForbiddenException>(() =>
            admin.UnassignAsync(stranger, binding.Id, NewUser("u"), default));
    }
}