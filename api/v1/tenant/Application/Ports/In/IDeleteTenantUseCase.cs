namespace TenantService.Application.Ports.In;

public interface IDeleteTenantUseCase
{
    Task ExecuteAsync(Guid tenantId);
}