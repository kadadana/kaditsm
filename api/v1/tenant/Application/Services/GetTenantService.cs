namespace TenantService.Application.Services;

using TenantService.Application.DTOs;
using TenantService.Application.Ports.In;
using TenantService.Application.Ports.Out;
using TenantService.Domain.Exceptions;

public class GetTenantService : IGetTenantUseCase
{
    private readonly ITenantRepository _repository;

    public GetTenantService(ITenantRepository repository)
    {
        _repository = repository;
    }

    public async Task<TenantResponse> ExecuteAsync(Guid tenantId)
    {
        var tenant = await _repository.GetByIdAsync(tenantId)
            ?? throw new TenantNotFoundException(tenantId);

        return new TenantResponse(tenant.Id, tenant.Name!, tenant.Slug!, tenant.Status, tenant.CreatedAt);
    }
}