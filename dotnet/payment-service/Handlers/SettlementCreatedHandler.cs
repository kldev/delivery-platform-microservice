using Delivery.Platform.PaymentService.Events;
using Delivery.Platform.PaymentService.Messaging;

namespace Delivery.Platform.PaymentService.Handlers;

// ReSharper disable once ClassNeverInstantiated.Global
public sealed class SettlementCreatedHandler
    (ILogger<SettlementCreatedHandler> logger) : IEventHandler<SettlementCreated>
{
    public Task HandleAsync(SettlementCreated @event, CancellationToken cancellationToken)
    {
        logger.LogInformation("Settlement created");
        return Task.CompletedTask;
    }
}