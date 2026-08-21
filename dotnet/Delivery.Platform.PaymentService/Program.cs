using Delivery.Platform.Infrastructure;
using Delivery.Platform.PaymentService.Application;
using Delivery.Platform.PaymentService.Endpoints;
using Delivery.Platform.PaymentService.Events;
using Delivery.Platform.PaymentService.Messaging;
using Scalar.AspNetCore;

var builder = WebApplication.CreateBuilder(args);
{
    builder.Services.AddOpenApi();
    builder.Services.AddDatabase(builder.Configuration);
    builder.Services.AddKafka(
        builder.Configuration);

    builder.Services.AddEventHandlers();
    builder.Services.AddApplication();
}

var app = builder.Build();
{
    await app.MigrateDatabaseAsync();
    app.MapAppEndpoints();
    app.MapOpenApi();
    app.MapScalarApiReference(options =>
    {
        options
            .WithTitle("Payment Service API")
            .WithTheme(ScalarTheme.Default);
    });

//    app.UseHttpsRedirection();

    app.MapGet("/healthz", () => "UP")
        .ExcludeFromDescription();

    app.MapGet("/docs", () => Results.Redirect("scalar/v1", permanent: true)).ExcludeFromDescription();

    app.Run();
}
