namespace TenantService.Adapter.Out.Persistence;

using DotNetEnv;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Design;

public class TenantDbContextFactory : IDesignTimeDbContextFactory<TenantDbContext>
{
    public TenantDbContext CreateDbContext(string[] args)
    {
        Env.Load();

        var connectionString = Environment.GetEnvironmentVariable("ConnectionStrings__Postgres");

        var optionsBuilder = new DbContextOptionsBuilder<TenantDbContext>();
        optionsBuilder.UseNpgsql(connectionString);

        return new TenantDbContext(optionsBuilder.Options);
    }
}