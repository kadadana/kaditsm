namespace TenantService.Adapter.Out.Messaging;

using System.Text;
using System.Text.Json;
using Microsoft.Extensions.Configuration;
using RabbitMQ.Client;
using TenantService.Application.Ports.Out;

public class RabbitMqEventPublisher : IEventPublisher, IAsyncDisposable
{
    private readonly IConfiguration _configuration;
    private IConnection? _connection;
    private IChannel? _channel;
    private readonly string _exchange;
    private readonly SemaphoreSlim _initLock = new(1, 1);

    public RabbitMqEventPublisher(IConfiguration configuration)
    {
        _configuration = configuration;
        _exchange = configuration["RabbitMq:Exchange"]
            ?? throw new InvalidOperationException("RabbitMq:Exchange has not configurated.");
    }

    private async Task EnsureInitializedAsync()
    {
        if (_channel is not null) return;

        await _initLock.WaitAsync();
        try
        {
            if (_channel is not null) return;

            var factory = new ConnectionFactory
            {
                HostName = _configuration["RabbitMq:HostName"]!,
                Port = int.Parse(_configuration["RabbitMq:Port"] ?? "5672"),
                UserName = _configuration["RabbitMq:UserName"]!,
                Password = _configuration["RabbitMq:Password"]!
            };

            _connection = await factory.CreateConnectionAsync();
            _channel = await _connection.CreateChannelAsync();

            await _channel.ExchangeDeclareAsync(exchange: _exchange, type: ExchangeType.Topic, durable: true);
        }
        finally
        {
            _initLock.Release();
        }
    }

    public async Task PublishAsync<T>(T @event) where T : class
    {
        await EnsureInitializedAsync();

        var eventName = typeof(T).Name;
        var action = eventName.StartsWith("Tenant")
            ? eventName["Tenant".Length..].ToLowerInvariant()
            : eventName.ToLowerInvariant();

        var routingKey = $"tenant.tenant.{action}";

        var body = Encoding.UTF8.GetBytes(JsonSerializer.Serialize(@event));

        var properties = new BasicProperties
        {
            Persistent = true,
            ContentType = "application/json"
        };

        await _channel!.BasicPublishAsync(
            exchange: _exchange,
            routingKey: routingKey,
            mandatory: false,
            basicProperties: properties,
            body: body);
    }

    public async ValueTask DisposeAsync()
    {
        if (_channel is not null) await _channel.CloseAsync();
        if (_connection is not null) await _connection.CloseAsync();
    }
}