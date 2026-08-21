namespace Delivery.Platform.PaymentService.Events;

public interface IDeliveryEvent : IEvent
{
    Guid DeliveryId { get; }
}