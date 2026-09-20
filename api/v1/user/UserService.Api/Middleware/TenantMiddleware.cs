using UserService.Api.Security;
using UserService.Infrastructure.Tenancy;

namespace UserService.Api.Middleware;

public sealed class TenantMiddleware(RequestDelegate next)
{
    public async Task InvokeAsync(HttpContext context, TenantContext tenant)
    {
        if (context.User.Identity?.IsAuthenticated == true)
        {
            if (!context.User.TryGetIdentity(out _, out var tenantId))
            {
                context.Response.StatusCode = StatusCodes.Status403Forbidden;
                return;
            }

            tenant.Set(tenantId);
        }

        await next(context);
    }
}