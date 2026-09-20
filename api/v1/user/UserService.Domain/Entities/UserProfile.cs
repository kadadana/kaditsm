// UserService.Domain/Entities/UserProfile.cs
namespace UserService.Domain.Entities;

public class UserProfile
{
    public Guid Id { get; private set; }
    public Guid TenantId { get; private set; }
    public string DisplayName { get; private set; } = default!;
    public string? Title { get; private set; }
    public string Locale { get; private set; } = "en";
    public string Timezone { get; private set; } = "UTC";
    public Guid? AvatarFileId { get; private set; }
    public DateTimeOffset CreatedAt { get; private set; }
    public DateTimeOffset UpdatedAt { get; private set; }

    private UserProfile() { }

    public static UserProfile Create(Guid identityId, Guid tenantId, string displayName, DateTimeOffset now)
    {
        ArgumentException.ThrowIfNullOrWhiteSpace(displayName);

        return new UserProfile
        {
            Id = identityId,
            TenantId = tenantId,
            DisplayName = displayName.Trim(),
            CreatedAt = now,
            UpdatedAt = now
        };
    }

    public void UpdateDetails(string displayName, string? title, string locale, string timezone, DateTimeOffset now)
    {
        ArgumentException.ThrowIfNullOrWhiteSpace(displayName);

        DisplayName = displayName.Trim();
        Title = string.IsNullOrWhiteSpace(title) ? null : title.Trim();
        Locale = locale;
        Timezone = timezone;
        UpdatedAt = now;
    }
}