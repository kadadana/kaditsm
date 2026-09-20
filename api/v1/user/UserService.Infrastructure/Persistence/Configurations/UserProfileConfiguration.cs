using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using UserService.Domain.Entities;

namespace UserService.Infrastructure.Persistence.Configurations;

internal sealed class UserProfileConfiguration : IEntityTypeConfiguration<UserProfile>
{
    public void Configure(EntityTypeBuilder<UserProfile> b)
    {
        b.ToTable("user_profiles");

        b.HasKey(p => p.Id);
        b.Property(p => p.Id).ValueGeneratedNever(); // ID'yi auth-service veriyor

        b.Property(p => p.TenantId).IsRequired();
        b.Property(p => p.DisplayName).HasMaxLength(100).IsRequired();
        b.Property(p => p.Title).HasMaxLength(100);
        b.Property(p => p.Locale).HasMaxLength(16).IsRequired();
        b.Property(p => p.Timezone).HasMaxLength(64).IsRequired();

        b.HasIndex(p => new { p.TenantId, p.DisplayName });
    }
}