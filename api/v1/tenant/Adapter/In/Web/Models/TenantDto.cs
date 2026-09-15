namespace TenantService.Adapter.In.Web.Models;

public record TenantDto(Guid Id, string Name, string Slug, string Status, DateTime CreatedAt);