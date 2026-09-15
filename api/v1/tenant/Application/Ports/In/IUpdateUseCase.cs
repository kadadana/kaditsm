namespace TenantService.Application.Ports.In;

using TenantService.Application.DTOs;

public interface IUpdateTenantUseCase
{
    Task<TenantResponse> ExecuteAsync(Guid tenantId, UpdateTenantCommand command);
}