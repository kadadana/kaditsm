// UserService.Infrastructure/Repositories/UserProfileRepository.cs
using Microsoft.EntityFrameworkCore;
using UserService.Application.Abstractions;
using UserService.Domain.Entities;
using UserService.Infrastructure.Persistence;

namespace UserService.Infrastructure.Repositories;

internal sealed class UserProfileRepository(UserDbContext db) : IUserProfileRepository
{
    public Task<UserProfile?> GetByIdAsync(Guid id, CancellationToken ct) =>
        db.UserProfiles.FirstOrDefaultAsync(p => p.Id == id, ct);

    public Task<bool> ExistsAsync(Guid id, CancellationToken ct) =>
        db.UserProfiles.AnyAsync(p => p.Id == id, ct);

    public async Task<(IReadOnlyList<UserProfile> Items, int TotalCount)> SearchAsync(
        string? term, int page, int pageSize, CancellationToken ct)
    {
        var query = db.UserProfiles.AsNoTracking();

        if (!string.IsNullOrWhiteSpace(term))
        {
            var escaped = term.Replace("\\", "\\\\").Replace("%", "\\%").Replace("_", "\\_");
            query = query.Where(p => EF.Functions.ILike(p.DisplayName, $"%{escaped}%"));
        }

        var total = await query.CountAsync(ct);

        var items = await query
            .OrderBy(p => p.DisplayName).ThenBy(p => p.Id)
            .Skip((page - 1) * pageSize)
            .Take(pageSize)
            .ToListAsync(ct);

        return (items, total);
    }

    public async Task AddAsync(UserProfile profile, CancellationToken ct) =>
        await db.UserProfiles.AddAsync(profile, ct);

    public Task SaveChangesAsync(CancellationToken ct) => db.SaveChangesAsync(ct);
}