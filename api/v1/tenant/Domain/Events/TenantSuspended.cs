namespace TenantService.Domain.Events;

public record TenantSuspended(Guid TenantId, DateTime OccurredAt);