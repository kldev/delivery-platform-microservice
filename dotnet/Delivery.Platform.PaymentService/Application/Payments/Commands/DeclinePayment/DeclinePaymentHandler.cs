using Delivery.Platform.Infrastructure.Persistence;
using Delivery.Platform.PaymentService.Infrastructure.Persistence;

namespace Delivery.Platform.PaymentService.Application.Payments.Commands.DeclinePayment;

public sealed class DeclinePaymentHandler(PaymentDbContext dbContext)
{
    public async Task Handle(
        DeclinePaymentCommand command,
        CancellationToken cancellationToken)
    {
        var payment = await dbContext.Payments
            .FindAsync([command.PaymentId], cancellationToken);

        if (payment is null)
            throw new InvalidOperationException(
                $"Payment '{command.PaymentId}' was not found.");

        payment.MarkAsFailed();

        await dbContext.SaveChangesAsync(cancellationToken);
    }
}