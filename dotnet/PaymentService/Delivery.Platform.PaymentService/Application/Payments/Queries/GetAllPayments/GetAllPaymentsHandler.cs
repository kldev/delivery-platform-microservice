using Delivery.Platform.Infrastructure.Persistence;
using Delivery.Platform.PaymentService.Infrastructure.Persistence;
using Microsoft.EntityFrameworkCore;

namespace Delivery.Platform.PaymentService.Application.Payments.Queries.GetAllPayments;

public sealed class GetAllPaymentsHandler(
    PaymentDbContext dbContext)
{
    public async Task<IReadOnlyList<PaymentItemResponse>> Handle(
        GetAllPaymentsQuery query,
        CancellationToken cancellationToken = default)
    {
        return await dbContext.Payments
            .AsNoTracking()
            .WithStatus(query.Status)
            .OrderByDescending(x => x.CreatedAt)
            .Select(x => new PaymentItemResponse(
                x.Id,
                x.DeliveryId,
                x.Amount,
                x.Currency,
                x.Status.ToString(),
                x.CreatedAt,
                x.PaidAt)).ToListAsync(cancellationToken);
    }
}