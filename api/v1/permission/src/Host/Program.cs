using DotNetEnv;
using Microsoft.AspNetCore.Server.Kestrel.Core;
using PermissionService.Application;
using PermissionService.Adapters.SpiceDb;
using PermissionService.Api.Grpc;

Env.NoClobber().TraversePath().Load();

var builder = WebApplication.CreateBuilder(args);

int Port(string key) =>
    builder.Configuration.GetValue<int?>(key)
    ?? throw new InvalidOperationException($"Missing config: {key}");

var restPort = Port("Ports:Rest");
var grpcPort = Port("Ports:Grpc");

builder.WebHost.ConfigureKestrel(o =>
{
    o.ListenAnyIP(restPort, l => l.Protocols = HttpProtocols.Http1);
    o.ListenAnyIP(grpcPort, l => l.Protocols = HttpProtocols.Http2);
});

builder.Services.AddScoped<ICheckPermission, CheckPermission>();
builder.Services.Configure<SpiceDbOptions>(builder.Configuration.GetSection("SpiceDb"));
builder.Services.AddSingleton<IPermissionStore, SpiceDbStore>();
builder.Services.AddGrpc(o => o.Interceptors.Add<ExceptionInterceptor>());

var app = builder.Build();

app.MapGet("/health", () => "ok").RequireHost($"*:{restPort}");
app.MapGrpcService<PermissionCheckService>().RequireHost($"*:{grpcPort}");

app.Run();