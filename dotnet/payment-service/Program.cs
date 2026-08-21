using Delivery.Platform.PaymentService;
using Delivery.Platform.PaymentService.Messaging;

var builder = WebApplication.CreateBuilder(args);
{
    builder.Services.AddOpenApi();

    builder.Services.AddKafka(
        builder.Configuration);

    builder.Services.AddEventHandlers();
}

var app = builder.Build();
{
    if (app.Environment.IsDevelopment())
    {
        app.MapOpenApi();
    }

    app.UseHttpsRedirection();

    app.MapGet("/healthz", () => "UP")
        .ExcludeFromDescription();

    app.Run();
}
