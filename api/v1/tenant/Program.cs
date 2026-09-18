using DotNetEnv;
using Microsoft.EntityFrameworkCore;
using TenantService.Adapter.Out.Messaging;
using TenantService.Adapter.Out.Persistence;
using TenantService.Application.Ports.In;
using TenantService.Application.Ports.Out;
using TenantService.Application.Services;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi;
using Scalar.AspNetCore;
using Microsoft.IdentityModel.Protocols.OpenIdConnect;

var builder = WebApplication.CreateBuilder(args);

if (builder.Environment.IsDevelopment())
{
    Env.Load();
}

builder.Services.AddControllers();

builder.Services.AddHttpClient("JwksClient");

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        var jwksUrl = builder.Configuration["Jwt:JwksUrl"]!;

        options.RequireHttpsMetadata = builder.Environment.IsProduction();
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidIssuer = builder.Configuration["Jwt:Issuer"],
            ValidateAudience = false,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,

            IssuerSigningKeyResolver = (token, securityToken, kid, validationParameters) =>
            {
                using var client = new HttpClient();
                var json = client.GetStringAsync(jwksUrl).GetAwaiter().GetResult();
                var jwks = new JsonWebKeySet(json);
                return jwks.GetSigningKeys();
            }
        };
    });

builder.Services.AddAuthorization(options =>
{
    options.AddPolicy("ServiceOnly", policy =>
        policy.RequireClaim("role", "service"));

    options.AddPolicy("PlatformAdmin", policy =>
        policy.RequireClaim("role", "platform_admin"));

    options.AddPolicy("TenantMember", policy =>
        policy.RequireAuthenticatedUser());
});
builder.Services.AddDbContext<TenantDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("Postgres")));
builder.Services.AddAuthorization();

// Ports / Adapters
builder.Services.AddScoped<ITenantRepository, EfTenantRepository>();
builder.Services.AddSingleton<IEventPublisher, RabbitMqEventPublisher>();

// Use Cases
builder.Services.AddScoped<ICreateTenantUseCase, CreateTenantService>();
builder.Services.AddScoped<IGetTenantUseCase, GetTenantService>();
builder.Services.AddScoped<IUpdateTenantUseCase, UpdateTenantService>();
builder.Services.AddScoped<ISuspendTenantUseCase, SuspendTenantService>();
builder.Services.AddScoped<IReactivateTenantUseCase, ReactivateTenantService>();
builder.Services.AddScoped<IDeleteTenantUseCase, DeleteTenantService>();

// Swagger
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddOpenApi(options =>
{
    options.AddDocumentTransformer((document, context, cancellationToken) =>
    {
        document.Components ??= new OpenApiComponents();
        document.Components.SecuritySchemes ??= new Dictionary<string, IOpenApiSecurityScheme>();

        document.Components.SecuritySchemes["Bearer"] = new OpenApiSecurityScheme
        {
            Type = SecuritySchemeType.Http,
            Scheme = "Bearer",
            BearerFormat = "JWT",
            Description = "Enter JWT token"
        };

        document.Security ??= new List<OpenApiSecurityRequirement>();
        document.Security.Add(new OpenApiSecurityRequirement
        {
            [new OpenApiSecuritySchemeReference("Bearer", document)] = []
        });

        return Task.CompletedTask;
    });
});

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
    app.MapScalarApiReference();// /scalar/v1
}

app.UseAuthentication();
app.UseAuthorization();
app.MapControllers();

app.Run();