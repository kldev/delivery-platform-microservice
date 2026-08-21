namespace Delivery.Platform.PaymentService.Events;

public interface IDeliveryEvent
{
    Guid EventId { get; }
    Guid DeliveryId { get; }
    DateTimeOffset OccurredAt { get; }
}