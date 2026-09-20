namespace UserService.Domain.Exceptions;

public sealed class UserProfileNotFoundException(Guid id)
    : DomainException($"User profile '{id}' was not found.");