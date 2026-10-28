using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Routing;
using PermissionService.Application;
using PermissionService.Domain;
using System.Security.Claims;

namespace PermissionService.Api.Rest;

public sealed record CreateRoleRequest(string Name, string[] Actions);
public sealed record UpdateActionsRequest(string[] Actions);
public sealed record ObjectDto(string Type, string Id);
public sealed record SubjectDto(string Type, string Id, string? Relation);
public sealed record BindRoleRequest(Guid RoleId, ObjectDto Scope);
public sealed record AssignRequest(SubjectDto Subject);

public static class RestEndpoints
{
    public static IEndpointRouteBuilder MapPermissionRest(this IEndpointRouteBuilder app, int restPort)
    {
        var g = app.MapGroup("/v1").RequireHost($"*:{restPort}").RequireAuthorization("ManagementApi");

        g.MapPost("/roles", async (CreateRoleRequest r, HttpContext http,
            RoleAdministration admin, CancellationToken ct) =>
        {
            var role = await admin.CreateRoleAsync(Actor(http), r.Name, r.Actions, ct);
            return Results.Created($"/v1/roles/{role.Id}", new { role.Id, role.Name, role.Actions });
        });

        g.MapPut("/roles/{id:guid}/actions", async (Guid id, UpdateActionsRequest r, HttpContext http,
            RoleAdministration admin, CancellationToken ct) =>
        {
            var token = await admin.UpdateRoleActionsAsync(Actor(http), id, r.Actions, ct);
            return Results.Ok(new { consistencyToken = token });
        });

        g.MapGet("/roles", async (RoleAdministration admin, CancellationToken ct) =>
                   Results.Ok((await admin.ListRolesAsync(ct)).Select(r => new { r.Id, r.Name, r.Actions })));

        g.MapGet("/roles/{id:guid}/bindings", async (Guid id, HttpContext http,
            RoleAdministration admin, CancellationToken ct) =>
            Results.Ok((await admin.BindingsOfRoleAsync(Actor(http), id, ct))
                .Select(b => new { bindingId = b.Id, scope = new { type = b.ScopeType, id = b.ScopeId } })));

        g.MapDelete("/roles/{id:guid}", async (Guid id, HttpContext http,
            RoleAdministration admin, CancellationToken ct) =>
        {
            await admin.DeleteRoleAsync(Actor(http), id, ct);
            return Results.NoContent();
        });

        g.MapPost("/bindings", async (BindRoleRequest r, HttpContext http,
            RoleAdministration admin, CancellationToken ct) =>
        {
            var (binding, token) = await admin.BindRoleAsync(
                Actor(http), r.RoleId, new ObjectRef(r.Scope.Type, r.Scope.Id), ct);
            return Results.Created($"/v1/bindings/{binding.Id}",
                new { bindingId = binding.Id, consistencyToken = token });
        });

        g.MapPost("/bindings/{id:guid}/assignments", async (Guid id, AssignRequest r, HttpContext http,
            RoleAdministration admin, CancellationToken ct) =>
        {
            var subject = new SubjectRef(new ObjectRef(r.Subject.Type, r.Subject.Id),
                string.IsNullOrWhiteSpace(r.Subject.Relation) ? null : r.Subject.Relation);
            var token = await admin.AssignAsync(Actor(http), id, subject, ct);
            return Results.Ok(new { consistencyToken = token });
        });

        g.MapDelete("/bindings/{id:guid}", async (Guid id, HttpContext http,
            RoleAdministration admin, CancellationToken ct) =>
            Results.Ok(new { consistencyToken = await admin.UnbindAsync(Actor(http), id, ct) }));

        g.MapDelete("/bindings/{id:guid}/assignments/{type}/{subjectId}", async (
            Guid id, string type, string subjectId, string? relation, HttpContext http,
            RoleAdministration admin, CancellationToken ct) =>
        {
            var subject = new SubjectRef(new ObjectRef(type, subjectId),
                string.IsNullOrWhiteSpace(relation) ? null : relation);
            return Results.Ok(new { consistencyToken = await admin.UnassignAsync(Actor(http), id, subject, ct) });
        });

        g.MapGet("/actions", () => Results.Ok(ActionCatalog.Snapshot()));

        g.MapGet("/access/{type}/{id}", async (string type, string id, HttpContext http,
            RoleAdministration admin, CancellationToken ct) =>
        {
            var entries = await admin.GetAccessAsync(Actor(http), new ObjectRef(type, id), ct);
            return Results.Ok(entries.Select(e => new
            {
                e.BindingId,
                e.RoleId,
                e.RoleName,
                e.Actions,
                members = e.Members.Select(m => new { type = m.Object.Type, id = m.Object.Id, relation = m.Relation }),
            }));
        });

        return app;
    }

    private static SubjectRef Actor(HttpContext http) =>
     new(new ObjectRef("user", http.User.FindFirstValue("sub")!));
}