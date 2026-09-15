namespace TenantService.Adapter.Out.Persistence;

using Microsoft.EntityFrameworkCore;
using TenantService.Application.Ports.Out;
using TenantService.Domain;

public class EfTenantRepository : ITenantRepository
{
    private readonly TenantDbContext _context;

    public EfTenantRepository(TenantDbContext context)
    {
        _context = context;
    }

    public async Task<Tenant?> GetByIdAsync(Guid id) =>
        await _context.Tenants.FirstOrDefaultAsync(t => t.Id == id);

    public async Task<Tenant?> GetBySlugAsync(string slug) =>
        await _context.Tenants.FirstOrDefaultAsync(t => t.Slug == slug);

    public async Task<bool> SlugExistsAsync(string slug) =>
        await _context.Tenants.AnyAsync(t => t.Slug == slug);

    public async Task AddAsync(Tenant tenant)
    {
        await _context.Tenants.AddAsync(tenant);
        await _context.SaveChangesAsync();
    }

    public async Task UpdateAsync(Tenant tenant)
    {
        _context.Tenants.Update(tenant);
        await _context.SaveChangesAsync();
    }
}