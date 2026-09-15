namespace TenantService.Domain;

public class Tenant
{
    public Guid Id { get; private set; }
    public string? Name { get; private set; }
    public string? Slug { get; private set; }
    public TenantStatus Status { get; private set; }
    public DateTime CreatedAt { get; private set; }

    private Tenant() { }

    private Tenant(Guid id, string name, string slug)
    {
        Id = id;
        Name = name;
        Slug = slug;
        Status = TenantStatus.Active;
        CreatedAt = DateTime.UtcNow;
    }

    public static Tenant Create(string name, string slug)
    {
        if (string.IsNullOrWhiteSpace(name))
            throw new ArgumentException("Tenant name can not be null.");

        if (string.IsNullOrWhiteSpace(slug) || !IsValidSlug(slug))
            throw new InvalidTenantSlugException(slug);

        return new Tenant(Guid.NewGuid(), name, slug.ToLowerInvariant());
    }

    public void Suspend()
    {
        if (Status != TenantStatus.Active)
            throw new InvalidOperationException($"Only an active tenant can be suspended. Now: {Status}");

        Status = TenantStatus.Suspended;
    }

    public void Reactivate()
    {
        if (Status != TenantStatus.Suspended)
            throw new InvalidOperationException($"Only an suspended tenant can be reactivated.. Now: {Status}");

        Status = TenantStatus.Active;
    }

    public void Delete()
    {
        if (Status == TenantStatus.Deleted)
            throw new InvalidOperationException("Tenant already deleted.");

        Status = TenantStatus.Deleted;
    }

    public void Rename(string newName)
    {
        if (string.IsNullOrWhiteSpace(newName))
            throw new ArgumentException("Tenant name can not be null.");

        Name = newName;
    }

    private static bool IsValidSlug(string slug) =>
        System.Text.RegularExpressions.Regex.IsMatch(slug, "^[a-z0-9][a-z0-9-]{1,48}[a-z0-9]$");
}