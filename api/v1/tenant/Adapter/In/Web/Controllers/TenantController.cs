namespace TenantService.Adapter.In.Web.Controllers;

using Microsoft.AspNetCore.Mvc;
using TenantService.Adapter.In.Web.Mappers;
using TenantService.Adapter.In.Web.Models;
using TenantService.Application.Ports.In;
using TenantService.Domain;
using TenantService.Domain.Exceptions;

[ApiController]
[Route("")]
public class TenantsController : ControllerBase
{
    private readonly ICreateTenantUseCase _createTenant;
    private readonly IGetTenantUseCase _getTenant;
    private readonly IUpdateTenantUseCase _updateTenant;
    private readonly ISuspendTenantUseCase _suspendTenant;
    private readonly IReactivateTenantUseCase _reactivateTenant;
    private readonly IDeleteTenantUseCase _deleteTenant;

    public TenantsController(
        ICreateTenantUseCase createTenant,
        IGetTenantUseCase getTenant,
        IUpdateTenantUseCase updateTenant,
        ISuspendTenantUseCase suspendTenant,
        IReactivateTenantUseCase reactivateTenant,
        IDeleteTenantUseCase deleteTenant)
    {
        _createTenant = createTenant;
        _getTenant = getTenant;
        _updateTenant = updateTenant;
        _suspendTenant = suspendTenant;
        _reactivateTenant = reactivateTenant;
        _deleteTenant = deleteTenant;
    }

    [HttpPost]
    public async Task<ActionResult<TenantDto>> Create([FromBody] CreateTenantRequest request)
    {
        try
        {
            var result = await _createTenant.ExecuteAsync(TenantWebMapper.ToCommand(request));
            var dto = TenantWebMapper.ToDto(result);
            return CreatedAtAction(nameof(GetById), new { id = dto.Id }, dto);
        }
        catch (SlugAlreadyTakenException ex)
        {
            return Conflict(new { message = ex.Message });
        }
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<TenantDto>> GetById(Guid id)
    {
        try
        {
            var result = await _getTenant.ExecuteAsync(id);
            return Ok(TenantWebMapper.ToDto(result));
        }
        catch (TenantNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
    }

    [HttpPatch("{id}")]
    public async Task<ActionResult<TenantDto>> Update(Guid id, [FromBody] UpdateTenantRequest request)
    {
        try
        {
            var result = await _updateTenant.ExecuteAsync(id, TenantWebMapper.ToCommand(request));
            return Ok(TenantWebMapper.ToDto(result));
        }
        catch (TenantNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
    }

    [HttpPost("{id}/suspend")]
    public async Task<IActionResult> Suspend(Guid id)
    {
        try
        {
            await _suspendTenant.ExecuteAsync(id);
            return NoContent();
        }
        catch (TenantNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
        catch (InvalidOperationException ex)
        {
            return Conflict(new { message = ex.Message });
        }
    }

    [HttpPost("{id}/activate")]
    public async Task<IActionResult> Reactivate(Guid id)
    {
        try
        {
            await _reactivateTenant.ExecuteAsync(id);
            return NoContent();
        }
        catch (TenantNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
        catch (InvalidOperationException ex)
        {
            return Conflict(new { message = ex.Message });
        }
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(Guid id)
    {
        try
        {
            await _deleteTenant.ExecuteAsync(id);
            return NoContent();
        }
        catch (TenantNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
        catch (InvalidOperationException ex)
        {
            return Conflict(new { message = ex.Message });
        }
    }
}