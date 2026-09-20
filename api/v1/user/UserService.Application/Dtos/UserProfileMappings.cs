using UserService.Domain.Entities;

namespace UserService.Application.Dtos;

internal static class UserProfileMappings
{
    public static UserProfileDto ToDto(this UserProfile p) => new(
        p.Id, p.DisplayName, p.Title, p.Locale, p.Timezone,
        p.AvatarFileId, p.CreatedAt, p.UpdatedAt);
}