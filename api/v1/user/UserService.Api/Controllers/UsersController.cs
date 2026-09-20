using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using UserService.Api.Security;
using UserService.Application.Abstractions;
using UserService.Application.Dtos;

namespace UserService.Api.Controllers;

[ApiController]
[Authorize]
[Route("users")]    
public sealed class UsersController(IUserProfileService service) : ControllerBase
{
    [HttpGet("me")]
    public Task<UserProfileDto> GetMe(CancellationToken ct) =>
        service.GetAsync(User.GetIdentityId(), ct);

    [HttpPut("me")]
    public Task<UserProfileDto> UpdateMe(UpdateUserProfileRequest request, CancellationToken ct) =>
        service.UpdateAsync(User.GetIdentityId(), request, ct);

    [HttpGet("{id:guid}")]
    public Task<UserProfileDto> GetById(Guid id, CancellationToken ct) =>
        service.GetAsync(id, ct);

    [HttpGet]
    public Task<PagedResult<UserProfileDto>> Search(
        [FromQuery] string? q, [FromQuery] int page = 1, [FromQuery] int pageSize = 20, CancellationToken ct = default) =>
        service.SearchAsync(q, page, pageSize, ct);
}