namespace TenantService.Application.Ports.In;

using TenantService.Application.DTOs;

public interface ICreateTenantUseCase
{
    Task<TenantResponse> ExecuteAsync(CreateTenantCommand command);
}