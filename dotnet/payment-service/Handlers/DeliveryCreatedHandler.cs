using Delivery.Platform.PaymentService.Events;
using Delivery.Platform.PaymentService.Messaging;

namespace Delivery.Platform.PaymentService.Handlers;

// ReSharper disable once ClassNeverInstantiated.Global
public sealed class DeliveryCreatedHandler
    (ILogger<SettlementCreatedHandler> logger) : IEventHandler<DeliveryCreated>
{
    public Task HandleAsync(DeliveryCreated @event, CancellationToken cancellationToken)
    {
        logger.LogInformation("Delivery created");
        logger.LogInformation($"Driver {@event.DriverId}, delivery id {@event.DeliveryId}");
        return Task.CompletedTask;
    }
}