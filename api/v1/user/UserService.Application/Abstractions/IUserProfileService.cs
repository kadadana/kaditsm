using UserService.Application.Dtos;

namespace UserService.Application.Abstractions;

public interface IUserProfileService
{
    Task<UserProfileDto> GetAsync(Guid id, CancellationToken ct);
    Task<UserProfileDto> UpdateAsync(Guid id, UpdateUserProfileRequest request, CancellationToken ct);
    Task<PagedResult<UserProfileDto>> SearchAsync(string? term, int page, int pageSize, CancellationToken ct);
    Task EnsureCreatedAsync(Guid identityId, Guid tenantId, string displayName, CancellationToken ct);
}