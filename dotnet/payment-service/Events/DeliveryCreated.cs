namespace Delivery.Platform.PaymentService.Events;

public sealed record DeliveryCreated(
    Guid EventId,
    Guid DeliveryId,
    Guid DriverId,
    DateTimeOffset OccurredAt
) : IDeliveryEvent;