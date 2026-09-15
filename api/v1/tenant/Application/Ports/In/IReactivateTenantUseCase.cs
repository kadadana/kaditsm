namespace TenantService.Application.Ports.In;

public interface IReactivateTenantUseCase
{
    Task ExecuteAsync(Guid tenantId);
}