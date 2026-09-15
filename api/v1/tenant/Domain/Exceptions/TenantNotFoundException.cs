namespace TenantService.Domain.Exceptions;

public class TenantNotFoundException : Exception
{
    public TenantNotFoundException(Guid tenantId)
        : base($"Tenant not found: {tenantId}")
    {
    }
}