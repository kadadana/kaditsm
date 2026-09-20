using Microsoft.AspNetCore.Authentication.JwtBearer;
using UserService.Api.Middleware;
using UserService.Application;
using UserService.Infrastructure;
using Microsoft.EntityFrameworkCore;
using UserService.Infrastructure.Persistence;
using UserService.Api.Security;

using DotNetEnv;

if (Environment.GetEnvironmentVariable("ASPNETCORE_ENVIRONMENT") == "Development")
    Env.NoClobber().TraversePath().Load();

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddApplication();
builder.Services.AddInfrastructure(builder.Configuration);

builder.Services.AddControllers();
builder.Services.AddProblemDetails();
builder.Services.AddExceptionHandler<GlobalExceptionHandler>();
builder.Services.AddHealthChecks();

builder.Services.AddJwtAuthentication(builder.Configuration);

builder.Services.AddAuthorization();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    using var scope = app.Services.CreateScope();
    scope.ServiceProvider.GetRequiredService<UserDbContext>().Database.Migrate();
}

app.UseExceptionHandler();
app.UseAuthentication();
app.UseAuthorization();
app.UseMiddleware<TenantMiddleware>();

app.MapHealthChecks("/health").AllowAnonymous();
app.MapControllers();

app.Run();