namespace UserService.Infrastructure.Messaging;

public sealed class KafkaOptions
{
    public string BootstrapServers { get; set; } = "localhost:9092";
    public string GroupId { get; set; } = "user-service";
    public string IdentityTopic { get; set; } = "identity.created";
}