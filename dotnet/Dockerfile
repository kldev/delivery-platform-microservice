FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build

WORKDIR /src

COPY dotnet/Delivery.Platform.slnx .

COPY dotnet/Delivery.Platform.Domain/Delivery.Platform.Domain.csproj Delivery.Platform.Domain/
COPY dotnet/Delivery.Platform.Infrastructure/Delivery.Platform.Infrastructure.csproj Delivery.Platform.Infrastructure/
COPY dotnet/Delivery.Platform.PaymentService/Delivery.Platform.PaymentService.csproj Delivery.Platform.PaymentService/

RUN dotnet restore Delivery.Platform.PaymentService/Delivery.Platform.PaymentService.csproj  --ignore-failed-sources

COPY dotnet/Delivery.Platform.Domain/ Delivery.Platform.Domain/
COPY dotnet/Delivery.Platform.Infrastructure/ Delivery.Platform.Infrastructure/
COPY dotnet/Delivery.Platform.PaymentService/ Delivery.Platform.PaymentService/

RUN dotnet publish \
    Delivery.Platform.PaymentService/Delivery.Platform.PaymentService.csproj \
    -c Release \
    -o /app/publish \
    --no-restore \
    /p:UseAppHost=false


FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS runtime

WORKDIR /app

COPY --from=build /app/publish .

ENTRYPOINT ["dotnet", "Delivery.Platform.PaymentService.dll"]
