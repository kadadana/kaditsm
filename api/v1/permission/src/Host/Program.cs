using DotNetEnv;
using Microsoft.AspNetCore.Server.Kestrel.Core;
using PermissionService.Application;
using PermissionService.Adapters.SpiceDb;
using PermissionService.Api.Grpc;
using PermissionService.Adapters.Postgres;
using PermissionService.Api.Rest;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using Microsoft.IdentityModel.Protocols;


Env.NoClobber().TraversePath().Load();

var builder = WebApplication.CreateBuilder(args);

int Port(string key) =>
    builder.Configuration.GetValue<int?>(key)
    ?? throw new InvalidOperationException($"Missing config: {key}");

var restPort = Port("Ports:Rest");
var grpcPort = Port("Ports:Grpc");
var jwt = builder.Configuration.GetSection("Jwt");
string Req(string k) => jwt[k] ?? throw new InvalidOperationException($"Missing config: Jwt:{k}");

var audience = jwt["Audience"];
if (string.IsNullOrWhiteSpace(audience) && !builder.Environment.IsDevelopment())
    throw new InvalidOperationException("Missing config: Jwt:Audience");

var jwks = new ConfigurationManager<JsonWebKeySet>(
    Req("JwksUrl"), new JwksRetriever(),
    new HttpDocumentRetriever { RequireHttps = !builder.Environment.IsDevelopment() });


builder.WebHost.ConfigureKestrel(o =>
{
    o.ListenAnyIP(restPort, l => l.Protocols = HttpProtocols.Http1);
    o.ListenAnyIP(grpcPort, l => l.Protocols = HttpProtocols.Http2);
});

builder.Services.AddScoped<ICheckPermission, CheckPermission>();
builder.Services.Configure<SpiceDbOptions>(builder.Configuration.GetSection("SpiceDb"));
builder.Services.AddSingleton<IPermissionStore, SpiceDbStore>();
builder.Services.AddGrpc(o => o.Interceptors.Add<ExceptionInterceptor>());
builder.Services.AddExceptionHandler<ApiExceptionHandler>();
builder.Services.AddProblemDetails();

builder.Services.AddPostgresAdapter(
    builder.Configuration.GetConnectionString("Permission")
    ?? throw new InvalidOperationException("Missing config: ConnectionStrings:Permission"));
builder.Services.AddScoped<RoleAdministration>();

builder.Services
    .AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(o =>
    {
        o.MapInboundClaims = false;
        o.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidIssuer = Req("Issuer"),
            ValidateAudience = !string.IsNullOrWhiteSpace(audience),
            ValidAudience = audience,
            ValidateLifetime = true,
            ClockSkew = TimeSpan.FromSeconds(30),
            ValidateIssuerSigningKey = true,
            ValidAlgorithms = ["RS256"],
            IssuerSigningKeyResolver = (_, _, kid, _) =>
            {
                var keys = jwks.GetConfigurationAsync(CancellationToken.None).GetAwaiter().GetResult().GetSigningKeys();
                if (kid is not null && !keys.Any(k => k.KeyId == kid))
                    jwks.RequestRefresh();
                return keys;
            },
        };
    });

builder.Services.AddAuthorizationBuilder()
    .AddPolicy("ManagementApi", p => p.RequireAuthenticatedUser().RequireClaim("sub"));

var app = builder.Build();

app.MapGet("/health", () => "ok").RequireHost($"*:{restPort}");
app.MapGrpcService<PermissionCheckService>().RequireHost($"*:{grpcPort}");
app.UseExceptionHandler();
app.UseAuthentication();
app.UseAuthorization();
app.MapPermissionRest(restPort);

app.Run();

sealed class JwksRetriever : IConfigurationRetriever<JsonWebKeySet>
{
    public async Task<JsonWebKeySet> GetConfigurationAsync(
        string address, IDocumentRetriever retriever, CancellationToken cancel) =>
        new(await retriever.GetDocumentAsync(address, cancel));
}