using System.Security.Claims;

namespace UserService.Api.Security;

public static class ClaimsPrincipalExtensions
{
    public static bool TryGetIdentity(this ClaimsPrincipal user, out Guid identityId, out Guid tenantId)
    {
        identityId = tenantId = Guid.Empty;

        return Guid.TryParse(user.FindFirstValue(ClaimNames.IdentityId), out identityId)
            && Guid.TryParse(user.FindFirstValue(ClaimNames.TenantId), out tenantId);
    }

    public static Guid GetIdentityId(this ClaimsPrincipal user) =>
        Guid.Parse(user.FindFirstValue(ClaimNames.IdentityId)!);
}