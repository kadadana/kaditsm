namespace UserService.Application.Abstractions;

public interface ITenantContext
{
    Guid? TenantId { get; }
}