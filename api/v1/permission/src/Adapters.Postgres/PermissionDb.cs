using System.Text.Json;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using PermissionService.Application;
using PermissionService.Domain;

namespace PermissionService.Adapters.Postgres;

public sealed class PermissionDb(DbContextOptions<PermissionDb> options) : DbContext(options), IUnitOfWork
{
    public DbSet<Role> Roles => Set<Role>();
    public DbSet<RoleBinding> Bindings => Set<RoleBinding>();
    public DbSet<AuditEntry> Audit => Set<AuditEntry>();

    public async Task CommitAsync(CancellationToken ct) => await SaveChangesAsync(ct);

    protected override void OnModelCreating(ModelBuilder b)
    {
        b.Entity<Role>(e =>
        {
            e.ToTable("roles");
            e.HasKey(x => x.Id);
            e.Property(x => x.Name).IsRequired().HasMaxLength(100);
            e.HasIndex(x => x.Name).IsUnique();
        });
        b.Entity<RoleBinding>(e =>
        {
            e.ToTable("role_bindings");
            e.HasKey(x => x.Id);
            e.Ignore(x => x.Scope);
            e.HasIndex(x => new { x.RoleId, x.ScopeType, x.ScopeId }).IsUnique();
            e.HasOne<Role>().WithMany().HasForeignKey(x => x.RoleId).OnDelete(DeleteBehavior.Restrict);
        });
        b.Entity<AuditEntry>(e =>
        {
            e.ToTable("audit_log");
            e.HasKey(x => x.Id);
            e.Property(x => x.DetailsJson).HasColumnType("jsonb");
            e.HasIndex(x => x.At);
        });
    }
}

internal sealed class RoleRepository(PermissionDb db) : IRoleRepository
{
    public Task<Role?> GetAsync(Guid id, CancellationToken ct) =>
        db.Roles.FirstOrDefaultAsync(r => r.Id == id, ct);
    public async Task AddAsync(Role role, CancellationToken ct) => await db.Roles.AddAsync(role, ct);
    public Task<RoleBinding?> GetBindingAsync(Guid id, CancellationToken ct) =>
        db.Bindings.FirstOrDefaultAsync(b => b.Id == id, ct);
    public async Task<IReadOnlyList<RoleBinding>> BindingsOfAsync(Guid roleId, CancellationToken ct) =>
        await db.Bindings.Where(b => b.RoleId == roleId).ToListAsync(ct);
    public async Task AddBindingAsync(RoleBinding binding, CancellationToken ct) =>
        await db.Bindings.AddAsync(binding, ct);
}

internal sealed class AuditLog(PermissionDb db) : IAuditLog
{
    public void Record(string actor, string action, string target, object details) =>
        db.Audit.Add(new AuditEntry(Guid.CreateVersion7(), DateTimeOffset.UtcNow,
            actor, action, target, JsonSerializer.Serialize(details)));
}

public static class DependencyInjection
{
    public static IServiceCollection AddPostgresAdapter(this IServiceCollection s, string connectionString)
    {
        s.AddDbContext<PermissionDb>(o => o.UseNpgsql(connectionString).UseSnakeCaseNamingConvention());
        s.AddScoped<IUnitOfWork>(sp => sp.GetRequiredService<PermissionDb>());
        s.AddScoped<IRoleRepository, RoleRepository>();
        s.AddScoped<IAuditLog, AuditLog>();
        return s;
    }
}