namespace TenantService.Application.Ports.In;

public interface ISuspendTenantUseCase
{
    Task ExecuteAsync(Guid tenantId);
}