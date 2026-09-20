using FluentValidation;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.DependencyInjection.Extensions;
using UserService.Application.Abstractions;
using UserService.Application.Services;
using UserService.Application.Validators;

namespace UserService.Application;

public static class DependencyInjection
{
    public static IServiceCollection AddApplication(this IServiceCollection services)
    {
        services.AddValidatorsFromAssemblyContaining<UpdateUserProfileRequestValidator>();
        services.AddScoped<IUserProfileService, UserProfileService>();
        services.TryAddSingleton(TimeProvider.System);
        return services;
    }
}