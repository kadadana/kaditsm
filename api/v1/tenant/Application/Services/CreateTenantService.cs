namespace TenantService.Application.Services;

using TenantService.Application.DTOs;
using TenantService.Application.Ports.In;
using TenantService.Application.Ports.Out;
using TenantService.Domain;
using TenantService.Domain.Events;
using TenantService.Domain.Exceptions;

public class CreateTenantService : ICreateTenantUseCase
{
    private readonly ITenantRepository _repository;
    private readonly IEventPublisher _eventPublisher;

    public CreateTenantService(ITenantRepository repository, IEventPublisher eventPublisher)
    {
        _repository = repository;
        _eventPublisher = eventPublisher;
    }

    public async Task<TenantResponse> ExecuteAsync(CreateTenantCommand command)
    {
        if (await _repository.SlugExistsAsync(command.Slug))
            throw new SlugAlreadyTakenException(command.Slug);

        var tenant = Tenant.Create(command.Name, command.Slug);

        await _repository.AddAsync(tenant);
        await _eventPublisher.PublishAsync(new TenantCreated(tenant.Id, tenant.Slug!, tenant.CreatedAt));

        return new TenantResponse(tenant.Id, tenant.Name!, tenant.Slug!, tenant.Status, tenant.CreatedAt);
    }
}