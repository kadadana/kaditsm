namespace TenantService.Domain;

public class InvalidTenantSlugException : Exception
{
    public InvalidTenantSlugException(string slug)
        : base($"Invalid tenant slug: '{slug}'. Can only contain lowercase letters, numbers, and hyphens; must be 3–50 characters long..")
    {
    }
}