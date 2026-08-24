using Delivery.Platform.PaymentService.Application.Payments.Commands.AcceptPayment;
using Delivery.Platform.PaymentService.Application.Payments.Commands.DeclinePayment;
using Delivery.Platform.PaymentService.Application.Payments.Queries.GetAllPayments;

namespace Delivery.Platform.PaymentService.Application;

public static class ApplicationExtensions
{
    public static IServiceCollection AddApplication(
        this IServiceCollection services)
    {
        services.AddScoped<AcceptPaymentHandler>();
        services.AddScoped<DeclinePaymentHandler>();
        services.AddScoped<GetPaymentsHandler>();

        return services;
    }
}