namespace UserService.Infrastructure.Messaging;

public sealed record IdentityCreatedEvent(
    Guid EventId,
    Guid IdentityId,
    Guid TenantId,
    string Email,
    string? DisplayName,
    DateTimeOffset OccurredAt);