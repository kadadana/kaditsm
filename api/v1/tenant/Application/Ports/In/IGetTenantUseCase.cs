namespace TenantService.Application.Ports.In;

using TenantService.Application.DTOs;

public interface IGetTenantUseCase
{
    Task<TenantResponse> ExecuteAsync(Guid tenantId);
}