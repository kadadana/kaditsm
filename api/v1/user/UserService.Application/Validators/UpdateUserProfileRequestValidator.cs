using FluentValidation;
using UserService.Application.Dtos;

namespace UserService.Application.Validators;

public sealed class UpdateUserProfileRequestValidator : AbstractValidator<UpdateUserProfileRequest>
{
    public UpdateUserProfileRequestValidator()
    {
        RuleFor(x => x.DisplayName).NotEmpty().MaximumLength(100);
        RuleFor(x => x.Title).MaximumLength(100);
        RuleFor(x => x.Locale).NotEmpty().Matches("^[a-z]{2}(-[A-Z]{2})?$")
            .WithMessage("Locale must look like 'en' or 'tr-TR'.");
        RuleFor(x => x.Timezone).NotEmpty().MaximumLength(64)
            .Must(id => TimeZoneInfo.TryFindSystemTimeZoneById(id, out _))
            .WithMessage("Unknown timezone id.");
    }
}