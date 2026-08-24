using Delivery.Platform.Domain.Payments;
using Delivery.Platform.PaymentService.Application.Payments.Commands.AcceptPayment;
using Delivery.Platform.PaymentService.Application.Payments.Commands.DeclinePayment;
using Delivery.Platform.PaymentService.Application.Payments.Queries.GetAllPayments;
using Microsoft.AspNetCore.Mvc;

namespace Delivery.Platform.PaymentService.Endpoints.Payment;

public static class Endpoint
{
    public static void Map(WebApplication app)
    {
        var group = app.MapGroup("/api/payments");

        group.MapPut("/{id}/accept",
            async (Guid id, [FromServices] AcceptPaymentHandler handler, CancellationToken ct) =>
            {
                await handler.Handle(new AcceptPaymentCommand(id), ct);
                return Results.NoContent();
            });

        group.MapPut("/{id}/decline",
            async (Guid id, [FromServices] DeclinePaymentHandler handler, CancellationToken ct) =>
            {
                await handler.Handle(new DeclinePaymentCommand(id), ct);
                return Results.NoContent();
            });

        group.MapGet("",
            async ([FromServices] GetPaymentsHandler handler, [FromQuery] PaymentStatus? status, [FromQuery] Guid? deliveryId,
                    CancellationToken ct) =>
                TypedResults.Ok(await handler.Handle(new GetPaymentsQuery(status, deliveryId), ct)));

    }
}