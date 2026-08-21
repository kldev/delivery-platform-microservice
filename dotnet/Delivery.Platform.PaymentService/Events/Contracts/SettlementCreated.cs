namespace Delivery.Platform.PaymentService.Events;

// ReSharper disable once ClassNeverInstantiated.Global
public sealed record SettlementCreated(Guid SettlementId, Guid DriverId);