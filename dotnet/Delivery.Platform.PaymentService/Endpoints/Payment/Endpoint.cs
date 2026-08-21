using Delivery.Platform.PaymentService.Application.Payments.Commands.AcceptPayment;
using Delivery.Platform.PaymentService.Application.Payments.Queries.GetAllPayments;
using Microsoft.AspNetCore.Mvc;

namespace Delivery.Platform.PaymentService.Endpoints.Payment;

public static class Endpoint
{
    public static void Map(WebApplication app)
    {
        var group = app.MapGroup("/api/payments");

        group.MapPut("/{id}/accept", async (Guid id, [FromServices] AcceptPaymentHandler handler, CancellationToken ct) =>
        {
            await handler.Handle(new AcceptPaymentCommand(id), ct);
            return Results.NoContent();
        });
        
        group.MapPut("/{id}/decline", async (Guid id, [FromServices] AcceptPaymentHandler handler, CancellationToken ct) =>
        {
            await handler.Handle(new AcceptPaymentCommand(id), ct);
            return Results.NoContent();
        });

        group.MapGet("",
            async ([FromServices] GetAllPaymentsHandler handler, CancellationToken ct) =>
            TypedResults.Ok(await handler.Handle(ct)));
    }
}