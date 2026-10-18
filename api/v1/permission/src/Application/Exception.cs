namespace PermissionService.Application;

public sealed class PermissionStoreUnavailableException(string message, Exception inner)
    : Exception(message, inner);