using Delivery.Platform.PaymentService.Events;
using Delivery.Platform.PaymentService.Handlers;
using Delivery.Platform.PaymentService.Messaging;

namespace Delivery.Platform.PaymentService;

public static class EventHandlerExtensions
{
    public static IServiceCollection AddEventHandlers(
        this IServiceCollection services)
    {
        services.AddKafkaListener<SettlementCreated, SettlementCreatedHandler>("settlement.created");
        services.AddKafkaListener<DeliveryCreated, DeliveryCreatedHandler>("delivery.created");

        return services;
    }
}