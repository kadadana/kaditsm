namespace TenantService.Application.Ports.Out;

using TenantService.Domain;

public interface ITenantRepository
{
    Task<Tenant?> GetByIdAsync(Guid id);
    Task<Tenant?> GetBySlugAsync(string slug);
    Task<bool> SlugExistsAsync(string slug);
    Task AddAsync(Tenant tenant);
    Task UpdateAsync(Tenant tenant);
}