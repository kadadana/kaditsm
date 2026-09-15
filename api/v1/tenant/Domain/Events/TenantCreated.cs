namespace TenantService.Domain.Events;

public record TenantCreated(Guid TenantId, string Slug, DateTime OccurredAt);