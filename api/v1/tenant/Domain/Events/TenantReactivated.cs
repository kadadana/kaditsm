namespace TenantService.Domain.Events;

public record TenantReactivated(Guid TenantId, DateTime OccurredAt);