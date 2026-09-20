using Microsoft.EntityFrameworkCore;
using UserService.Application.Abstractions;
using UserService.Domain.Entities;

namespace UserService.Infrastructure.Persistence;

public sealed class UserDbContext(DbContextOptions<UserDbContext> options, ITenantContext tenant)
    : DbContext(options)
{
    private Guid? CurrentTenantId => tenant.TenantId;

    public DbSet<UserProfile> UserProfiles => Set<UserProfile>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.ApplyConfigurationsFromAssembly(typeof(UserDbContext).Assembly);

        modelBuilder.Entity<UserProfile>().HasQueryFilter(p => p.TenantId == CurrentTenantId);
    }
}