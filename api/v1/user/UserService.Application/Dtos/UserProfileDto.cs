namespace UserService.Application.Dtos;

public sealed record UserProfileDto(
    Guid Id,
    string DisplayName,
    string? Title,
    string Locale,
    string Timezone,
    Guid? AvatarFileId,
    DateTimeOffset CreatedAt,
    DateTimeOffset UpdatedAt);

public sealed record UpdateUserProfileRequest(
    string DisplayName,
    string? Title,
    string Locale,
    string Timezone);

public sealed record PagedResult<T>(
    IReadOnlyList<T> Items,
    int Page,
    int PageSize,
    int TotalCount);