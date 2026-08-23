using Confluent.Kafka;

namespace Delivery.Platform.PaymentService.Messaging;

internal static class KafkaConsumerFactory
{
    public static IConsumer<string, string> Create(
        KafkaOptions options)
    {
        var config = new ConsumerConfig
        {
            BootstrapServers = options.BootstrapServers,
            GroupId = options.GroupId,
            EnableAutoCommit = options.EnableAutoCommit,
            AutoOffsetReset =
                Enum.Parse<AutoOffsetReset>(
                    options.AutoOffsetReset,
                    ignoreCase: true),

            EnablePartitionEof = false
        };

        return new ConsumerBuilder<string, string>(config)
            .Build();
    }
}