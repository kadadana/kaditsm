namespace TenantService.Application.DTOs;

using TenantService.Domain;

public record TenantResponse(Guid Id, string Name, string Slug, TenantStatus Status, DateTime CreatedAt);