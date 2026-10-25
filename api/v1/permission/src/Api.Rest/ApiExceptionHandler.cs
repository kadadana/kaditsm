using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using PermissionService.Application;

namespace PermissionService.Api.Rest;

public sealed class ApiExceptionHandler(ILogger<ApiExceptionHandler> log) : IExceptionHandler
{
    public async ValueTask<bool> TryHandleAsync(HttpContext ctx, Exception e, CancellationToken ct)
    {
        var (status, title) = e switch
        {
            ArgumentException => (400, "Invalid request"),
            ForbiddenException => (403, "Forbidden"),
            NotFoundException => (404, "Not found"),
            PermissionStoreUnavailableException => (503, "Permission store unavailable"),
            _ => (0, "")
        };
        if (status == 0) return false;

        if (status == 503) log.LogError(e, "Permission store unavailable");

        ctx.Response.StatusCode = status;
        await ctx.Response.WriteAsJsonAsync(new { title, detail = e.Message }, ct);
        return true;
    }
}