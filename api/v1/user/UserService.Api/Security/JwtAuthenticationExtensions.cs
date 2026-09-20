using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Protocols;
using Microsoft.IdentityModel.Tokens;

namespace UserService.Api.Security;

public static class JwtAuthenticationExtensions
{
    public static IServiceCollection AddJwtAuthentication(this IServiceCollection services, IConfiguration config)
    {
        var jwksUrl = config["Jwt:JwksUrl"] ?? throw new InvalidOperationException("Jwt:JwksUrl is missing.");
        var issuer = config["Jwt:Issuer"] ?? throw new InvalidOperationException("Jwt:Issuer is missing.");
        var requireHttps = config.GetValue("Jwt:RequireHttpsMetadata", true);

        var jwks = new ConfigurationManager<JsonWebKeySet>(
            jwksUrl, new JwksRetriever(), new HttpDocumentRetriever { RequireHttps = requireHttps })
        {
            AutomaticRefreshInterval = TimeSpan.FromHours(1)
        };

        services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
            .AddJwtBearer(options =>
            {
                options.MapInboundClaims = false;
                options.TokenValidationParameters = new TokenValidationParameters
                {
                    ValidateIssuer = true,
                    ValidIssuer = issuer,
                    ValidateAudience = false,
                    ValidateLifetime = true,
                    ValidateIssuerSigningKey = true,
                    IssuerSigningKeyResolver = (_, _, kid, _) => ResolveKeys(jwks, kid)
                };
            });

        return services;
    }

    private static List<SecurityKey> ResolveKeys(ConfigurationManager<JsonWebKeySet> jwks, string? kid)
    {
        var keys = Find(jwks, kid);

        if (keys.Count == 0)
        {
            jwks.RequestRefresh();
            keys = Find(jwks, kid);
        }

        return keys;
    }

    private static List<SecurityKey> Find(ConfigurationManager<JsonWebKeySet> jwks, string? kid)
    {
        var set = jwks.GetConfigurationAsync(CancellationToken.None).GetAwaiter().GetResult();
        return set.GetSigningKeys().Where(k => kid is null || k.KeyId == kid).ToList();
    }
}