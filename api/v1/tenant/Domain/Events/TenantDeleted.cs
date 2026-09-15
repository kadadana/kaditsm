namespace TenantService.Domain.Events;

public record TenantDeleted(Guid TenantId, DateTime OccurredAt);