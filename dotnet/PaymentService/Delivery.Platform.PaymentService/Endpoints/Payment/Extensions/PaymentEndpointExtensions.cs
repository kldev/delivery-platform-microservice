using Delivery.Platform.PaymentService.Application.Payments;

namespace Delivery.Platform.PaymentService.Endpoints.Payment.Extensions;

public static class PaymentEndpointExtensions
{
    public static RouteGroupBuilder MapPaymentActions(
        this RouteGroupBuilder group)
    {
        group.MapPut(
            "/{id}/{action}",
            async (
                Guid id,
                PaymentAction action,
                PaymentActionService service,
                CancellationToken ct) =>
            {
                await service.Execute(id, action, ct);

                return Results.NoContent();
            });

        return group;
    }
}
