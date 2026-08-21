using Delivery.Platform.Infrastructure.Persistence;
using Delivery.Platform.PaymentService.Infrastructure.Persistence;

namespace Delivery.Platform.PaymentService.Application.Payments.Commands.AcceptPayment;

public sealed class AcceptPaymentHandler(
    PaymentDbContext dbContext)
{
    public async Task Handle(
        AcceptPaymentCommand command,
        CancellationToken cancellationToken)
    {
        var payment = await dbContext.Payments
            .FindAsync([command.PaymentId], cancellationToken);

        if (payment is null)
            throw new InvalidOperationException(
                $"Payment '{command.PaymentId}' was not found.");

        payment.MarkAsPaid();

        await dbContext.SaveChangesAsync(cancellationToken);
    }
}