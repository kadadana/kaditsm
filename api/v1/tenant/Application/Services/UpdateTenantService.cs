namespace TenantService.Application.Services;

using TenantService.Application.DTOs;
using TenantService.Application.Ports.In;
using TenantService.Application.Ports.Out;
using TenantService.Domain.Exceptions;

public class UpdateTenantService : IUpdateTenantUseCase
{
    private readonly ITenantRepository _repository;

    public UpdateTenantService(ITenantRepository repository)
    {
        _repository = repository;
    }

    public async Task<TenantResponse> ExecuteAsync(Guid tenantId, UpdateTenantCommand command)
    {
        var tenant = await _repository.GetByIdAsync(tenantId)
            ?? throw new TenantNotFoundException(tenantId);

        tenant.Rename(command.Name);

        await _repository.UpdateAsync(tenant);

        return new TenantResponse(tenant.Id, tenant.Name!, tenant.Slug!, tenant.Status, tenant.CreatedAt);
    }
}