using System.Text.Json.Nodes;
using Delivery.Gateway.Web.Swagger;
using Scalar.AspNetCore;

var builder = WebApplication.CreateBuilder(args);
{
    builder.Services
        .AddReverseProxy()
        .LoadFromConfig(builder.Configuration.GetSection("ReverseProxy"));
    
    // HttpClient for swagger aggregation
    builder.Services.AddHttpClient("SwaggerClient", client =>
    {
        client.Timeout = TimeSpan.FromSeconds(10);
    });
    builder.Services.AddScoped<SwaggerAggregator>();
}
var app = builder.Build();
{
    app.MapScalarApiReference("/scalar", options =>
    {
        options.Title = "Delivery Platform API";

        options.AddDocument(
            "delivery",
            "Deliveries",
            "/api-docs/delivery/swagger.json",
            true);
        
        options.AddDocument(
            "payment",
            "Payments",
            "/api-docs/payment/swagger.json",
            true);
        
        options.AddDocument(
            "ledger",
            "Ledger",
            "/api-docs/ledger/swagger.json",
            true);
        
        options.AddDocument(
            "settlement",
            "Settlement",
            "/api-docs/settlement/swagger.json",
            true);
        
        options.AddDocument(
            "reconciliation",
            "Reconciliation",
            "/api-docs/reconciliation/swagger.json",
            true);
    });

    app.MapReverseProxy();
    
    app.MapGet("/", () => "UP");
    
    app.MapSwaggerEndpoints();
    app.Run();
}
