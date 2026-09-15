using DotNetEnv;
using Microsoft.EntityFrameworkCore;
using TenantService.Adapter.Out.Messaging;
using TenantService.Adapter.Out.Persistence;
using TenantService.Application.Ports.In;
using TenantService.Application.Ports.Out;
using TenantService.Application.Services;

var builder = WebApplication.CreateBuilder(args);

if (builder.Environment.IsDevelopment())
{
    Env.Load();
}

builder.Services.AddControllers();

builder.Services.AddDbContext<TenantDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("Postgres")));

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
builder.Services.AddSwaggerGen();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.MapControllers();

app.Run();