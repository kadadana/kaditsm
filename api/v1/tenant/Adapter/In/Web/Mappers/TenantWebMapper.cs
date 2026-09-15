namespace TenantService.Adapter.In.Web.Mappers;

using TenantService.Adapter.In.Web.Models;
using TenantService.Application.DTOs;

public static class TenantWebMapper
{
    public static TenantDto ToDto(TenantResponse response) =>
        new(response.Id, response.Name, response.Slug, response.Status.ToString(), response.CreatedAt);

    public static CreateTenantCommand ToCommand(CreateTenantRequest request) =>
        new(request.Name, request.Slug);

    public static UpdateTenantCommand ToCommand(UpdateTenantRequest request) =>
        new(request.Name);
}