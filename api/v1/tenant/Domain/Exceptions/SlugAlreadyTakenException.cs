namespace TenantService.Domain;

public class SlugAlreadyTakenException : Exception
{
    public SlugAlreadyTakenException(string slug)
        : base($"Invalid tenant slug: '{slug}'. Can only contain lowercase letters, numbers, and hyphens; must be 3–50 characters long..")
    {
    }
}