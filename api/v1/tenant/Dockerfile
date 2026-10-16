FROM mcr.microsoft.com/dotnet/sdk:10.0-preview-alpine AS builder
WORKDIR /src

COPY *.csproj ./
RUN --mount=type=cache,target=/root/.nuget/packages \
    dotnet restore

COPY . ./
RUN --mount=type=cache,target=/root/.nuget/packages \
    --mount=type=cache,target=/root/.local/share \
    dotnet publish -c Release -o /app/publish /p:UseAppHost=false --no-restore

FROM mcr.microsoft.com/dotnet/aspnet:10.0-preview-alpine AS final
WORKDIR /app

ENV ASPNETCORE_URLS=http://+:8082
ENV ASPNETCORE_ENVIRONMENT=Production

RUN apk --no-cache add tzdata curl

COPY --from=builder /app/publish .
EXPOSE 8082

ENTRYPOINT ["dotnet", "TenantService.dll"]