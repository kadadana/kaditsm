using System.Text.Json;
using Confluent.Kafka;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using UserService.Application.Abstractions;
using UserService.Infrastructure.Tenancy;

namespace UserService.Infrastructure.Messaging;

internal sealed class IdentityCreatedConsumer(
    IServiceScopeFactory scopes,
    IOptions<KafkaOptions> options,
    ILogger<IdentityCreatedConsumer> logger) : BackgroundService
{
    private static readonly JsonSerializerOptions Json = new(JsonSerializerDefaults.Web);
    protected override Task ExecuteAsync(CancellationToken ct) => Task.Run(() => RunAsync(ct), ct);

    private async Task RunAsync(CancellationToken ct)
    {
        var o = options.Value;

        var config = new ConsumerConfig
        {
            BootstrapServers = o.BootstrapServers,
            GroupId = o.GroupId,
            AutoOffsetReset = AutoOffsetReset.Earliest,
            EnableAutoCommit = false
        };

        using var consumer = new ConsumerBuilder<string, string>(config).Build();
        consumer.Subscribe(o.IdentityTopic);

        try
        {
            while (!ct.IsCancellationRequested)
            {
                ConsumeResult<string, string> result;
                try
                {
                    result = consumer.Consume(ct);
                }
                catch (ConsumeException ex)
                {
                    logger.LogError(ex, "Kafka consume error");
                    continue;
                }

                var evt = Deserialize(result.Message.Value);

                if (evt is not null)
                    await HandleWithRetryAsync(evt, ct);

                consumer.Commit(result);
            }
        }
        catch (OperationCanceledException) { }
        finally
        {
            consumer.Close();
        }
    }

    private IdentityCreatedEvent? Deserialize(string json)
    {
        try
        {
            var evt = JsonSerializer.Deserialize<IdentityCreatedEvent>(json, Json);
            if (evt is null || evt.IdentityId == Guid.Empty || evt.TenantId == Guid.Empty)
                throw new JsonException("Missing required fields.");
            return evt;
        }
        catch (JsonException ex)
        {
            logger.LogError(ex, "Skipping malformed identity.created message: {Payload}", json);
            return null;
        }
    }

    private async Task HandleWithRetryAsync(IdentityCreatedEvent evt, CancellationToken ct)
    {
        while (true)
        {
            try
            {
                using var scope = scopes.CreateScope();
                scope.ServiceProvider.GetRequiredService<TenantContext>().Set(evt.TenantId);

                var name = !string.IsNullOrWhiteSpace(evt.DisplayName)
                    ? evt.DisplayName
                    : evt.Email.Split('@')[0];

                await scope.ServiceProvider
                    .GetRequiredService<IUserProfileService>()
                    .EnsureCreatedAsync(evt.IdentityId, evt.TenantId, name, ct);

                return;
            }
            catch (Exception ex) when (!ct.IsCancellationRequested)
            {
                logger.LogWarning(ex, "Failed to process {IdentityId}, retrying in 5s", evt.IdentityId);
                await Task.Delay(TimeSpan.FromSeconds(5), ct);
            }
        }
    }
}