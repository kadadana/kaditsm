namespace PermissionService.Application;

public sealed class PermissionStoreUnavailableException(string message, Exception inner)
    : Exception(message, inner);

public sealed class ForbiddenException(string message) : Exception(message);
public sealed class NotFoundException(string message) : Exception(message);
public sealed class ConflictException(string message) : Exception(message);