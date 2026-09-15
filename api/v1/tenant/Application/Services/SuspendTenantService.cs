namespace TenantService.Application.Services;

using TenantService.Application.Ports.In;
using TenantService.Application.Ports.Out;
using TenantService.Domain.Events;
using TenantService.Domain.Exceptions;

public class SuspendTenantService : ISuspendTenantUseCase
{
    private readonly ITenantRepository _repository;
    private readonly IEventPublisher _eventPublisher;

    public SuspendTenantService(ITenantRepository repository, IEventPublisher eventPublisher)
    {
        _repository = repository;
        _eventPublisher = eventPublisher;
    }

    public async Task ExecuteAsync(Guid tenantId)
    {
        var tenant = await _repository.GetByIdAsync(tenantId)
            ?? throw new TenantNotFoundException(tenantId);

        tenant.Suspend();

        await _repository.UpdateAsync(tenant);
        await _eventPublisher.PublishAsync(new TenantSuspended(tenant.Id, DateTime.UtcNow));
    }
}