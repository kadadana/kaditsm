namespace PermissionService.Application;

public sealed class PermissionStoreUnavailableException(string message, Exception inner)
    : Exception(message, inner);

public sealed class ForbiddenException(string message) : Exception(message);