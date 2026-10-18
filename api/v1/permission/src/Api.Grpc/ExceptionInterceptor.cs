using Grpc.Core;
using Grpc.Core.Interceptors;
using Microsoft.Extensions.Logging;
using PermissionService.Application;

namespace PermissionService.Api.Grpc;

public sealed class ExceptionInterceptor(ILogger<ExceptionInterceptor> log) : Interceptor
{
    public override async Task<TResponse> UnaryServerHandler<TRequest, TResponse>(
        TRequest request, ServerCallContext context, UnaryServerMethod<TRequest, TResponse> next)
    {
        try { return await next(request, context); }
        catch (ArgumentException e)
        {
            throw new RpcException(new Status(StatusCode.InvalidArgument, e.Message));
        }
        catch (PermissionStoreUnavailableException e)
        {
            log.LogError(e, "SpiceDB unavailable");
            throw new RpcException(new Status(StatusCode.Unavailable, "permission store unavailable"));
        }
    }
}