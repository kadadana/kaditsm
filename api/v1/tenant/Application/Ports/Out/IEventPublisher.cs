namespace TenantService.Application.Ports.Out;

public interface IEventPublisher
{
    Task PublishAsync<T>(T @event) where T : class;
}