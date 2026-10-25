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

    [Fact]
    public async Task Changing_role_actions_propagates_to_existing_bindings()
    {
        await using var sp = Build();
        using var scope = sp.CreateScope();
        var admin = scope.ServiceProvider.GetRequiredService<RoleAdministration>();
        var store = scope.ServiceProvider.GetRequiredService<IPermissionStore>();

        var id = Guid.NewGuid().ToString("N");
        var project = new ObjectRef("project", $"p{id}");
        var user = new SubjectRef(new ObjectRef("user", $"u{id}"));

        var role = await admin.CreateRoleAsync("test", $"reviewer-{id}", ["view"], default);
        var (binding, _) = await admin.BindRoleAsync("test", role.Id, project, default);
        var t1 = await admin.AssignAsync("test", binding.Id, user, default);

        Assert.True(await store.CheckAsync(project, "view", user, t1, default));
        Assert.False(await store.CheckAsync(project, "edit", user, t1, default));

        var t2 = await admin.UpdateRoleActionsAsync("test", role.Id, ["view", "edit"], default);
        Assert.True(await store.CheckAsync(project, "edit", user, t2, default));

        var t3 = await admin.UpdateRoleActionsAsync("test", role.Id, ["edit"], default);
        Assert.False(await store.CheckAsync(project, "view", user, t3, default));
        Assert.True(await store.CheckAsync(project, "edit", user, t3, default));
    }

    [Fact]
    public async Task Binding_rejects_actions_that_do_not_exist_on_scope_type()
    {
        await using var sp = Build();
        using var scope = sp.CreateScope();
        var admin = scope.ServiceProvider.GetRequiredService<RoleAdministration>();
        var id = Guid.NewGuid().ToString("N");

        var role = await admin.CreateRoleAsync("test", $"mgr-{id}", ["view", "manage_members"], default);

        await Assert.ThrowsAsync<ArgumentException>(() =>
            admin.BindRoleAsync("test", role.Id, new ObjectRef("ticket", $"t{id}"), default));
    }
}