using System.Text.Json;
using Microsoft.Extensions.Options;

namespace Delivery.Platform.PaymentService.Messaging;

public static class KafkaExtensions
{
    public static IServiceCollection AddKafka(
        this IServiceCollection services,
        IConfiguration configuration)
    {
        services.Configure<KafkaOptions>(
            configuration.GetSection(
                KafkaOptions.SectionName));

        services.AddSingleton(
            new JsonSerializerOptions(
                JsonSerializerDefaults.Web));

        return services;
    }

    public static IServiceCollection AddKafkaListener<
        TEvent,
        THandler>(
        this IServiceCollection services,
        string topic)
        where THandler :
        class,
        IEventHandler<TEvent>
    {
        services.AddScoped<
            IEventHandler<TEvent>,
            THandler>();

        services.AddHostedService<
            KafkaListener<TEvent>>(provider =>
        {
            var options =
                provider
                    .GetRequiredService<
                        IOptions<KafkaOptions>>()
                    .Value;

            var consumer =
                KafkaConsumerFactory.Create(options);

            var jsonOptions =
                provider.GetRequiredService<
                    JsonSerializerOptions>();

            var scopeFactory =
                provider.GetRequiredService<
                    IServiceScopeFactory>();

            var logger =
                provider
                    .GetRequiredService<
                        ILogger<KafkaListener<TEvent>>>();

            return new KafkaListener<TEvent>(
                scopeFactory, consumer, topic, jsonOptions, logger);

        });

        return services;
    }
}