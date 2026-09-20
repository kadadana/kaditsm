using UserService.Domain.Entities;

namespace UserService.Application.Abstractions;

public interface IUserProfileRepository
{
    Task<UserProfile?> GetByIdAsync(Guid id, CancellationToken ct);
    Task<bool> ExistsAsync(Guid id, CancellationToken ct);
    Task<(IReadOnlyList<UserProfile> Items, int TotalCount)> SearchAsync(
        string? term, int page, int pageSize, CancellationToken ct);
    Task AddAsync(UserProfile profile, CancellationToken ct);
    Task SaveChangesAsync(CancellationToken ct);
}