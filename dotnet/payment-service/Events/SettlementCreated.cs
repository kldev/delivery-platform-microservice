namespace Delivery.Platform.PaymentService.Events;

public sealed record SettlementCreated(Guid SettlementId, Guid DriverId);