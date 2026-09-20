using FluentValidation;
using UserService.Application.Abstractions;
using UserService.Application.Dtos;
using UserService.Domain.Entities;
using UserService.Domain.Exceptions;

namespace UserService.Application.Services;

public sealed class UserProfileService(
    IUserProfileRepository repository,
    IValidator<UpdateUserProfileRequest> updateValidator,
    TimeProvider clock) : IUserProfileService
{
    public async Task<UserProfileDto> GetAsync(Guid id, CancellationToken ct)
    {
        var profile = await repository.GetByIdAsync(id, ct)
            ?? throw new UserProfileNotFoundException(id);

        return profile.ToDto();
    }

    public async Task<UserProfileDto> UpdateAsync(Guid id, UpdateUserProfileRequest request, CancellationToken ct)
    {
        await updateValidator.ValidateAndThrowAsync(request, ct);

        var profile = await repository.GetByIdAsync(id, ct)
            ?? throw new UserProfileNotFoundException(id);

        profile.UpdateDetails(request.DisplayName, request.Title, request.Locale, request.Timezone, clock.GetUtcNow());
        await repository.SaveChangesAsync(ct);

        return profile.ToDto();
    }

    public async Task<PagedResult<UserProfileDto>> SearchAsync(string? term, int page, int pageSize, CancellationToken ct)
    {
        page = Math.Max(page, 1);
        pageSize = Math.Clamp(pageSize, 1, 100);

        var (items, total) = await repository.SearchAsync(term?.Trim(), page, pageSize, ct);

        return new PagedResult<UserProfileDto>(items.Select(i => i.ToDto()).ToList(), page, pageSize, total);
    }

    public async Task EnsureCreatedAsync(Guid identityId, Guid tenantId, string displayName, CancellationToken ct)
    {
        if (await repository.ExistsAsync(identityId, ct))
            return;

        var profile = UserProfile.Create(identityId, tenantId, displayName, clock.GetUtcNow());
        await repository.AddAsync(profile, ct);
        await repository.SaveChangesAsync(ct);
    }
}