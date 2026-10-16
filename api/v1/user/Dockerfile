FROM mcr.microsoft.com/dotnet/sdk:10.0-alpine AS build
WORKDIR /src

COPY Directory.*.props ./
COPY UserService.Domain/*.csproj UserService.Domain/
COPY UserService.Application/*.csproj UserService.Application/
COPY UserService.Infrastructure/*.csproj UserService.Infrastructure/
COPY UserService.Api/*.csproj UserService.Api/

RUN --mount=type=cache,target=/root/.nuget/packages \
    dotnet restore UserService.Api/UserService.Api.csproj

COPY . .

RUN --mount=type=cache,target=/root/.nuget/packages \
    dotnet publish UserService.Api/UserService.Api.csproj \
    -c Release -o /app --no-restore

# Runtime stage
FROM mcr.microsoft.com/dotnet/aspnet:10.0-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /app .
ENTRYPOINT ["dotnet", "UserService.Api.dll"]