package com.kadadana.kaditsm.modules.role.controller;

//JAVA IMPORTS
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.UUID;

//CORE IMPORTS
import com.kadadana.kaditsm.core.model.ApiResponse;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.role.dto.RoleCreateDTO;
import com.kadadana.kaditsm.modules.role.dto.RoleResponseDTO;
import com.kadadana.kaditsm.modules.role.dto.RoleUpdateDTO;
import com.kadadana.kaditsm.modules.role.service.RoleService;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role management operations")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Create role", description = "Creates a new system role.")
    public ApiResponse<RoleResponseDTO> createRole(@RequestBody RoleCreateDTO request) {
        RoleResponseDTO response = roleService.createRole(request);
        return ApiResponse.success(response, "Role created successfully.");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", description = "Fetches details of a specific role.")
    public ApiResponse<RoleResponseDTO> getRoleById(@PathVariable() UUID id) {
        RoleResponseDTO response = roleService.getRoleById(id);
        return ApiResponse.success(response, "Role fetched successfully.");
    }

    @GetMapping
    @Operation(summary = "Get all roles", description = "Lists all defined roles.")
    public ApiResponse<List<RoleResponseDTO>> getAllRoles() {
        List<RoleResponseDTO> response = roleService.getAllRoles();
        return ApiResponse.success(response, "Roles fetched successfully.");
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update role", description = "Updates an existing role's details.")
    public ApiResponse<RoleResponseDTO> updateRole(
            @PathVariable() UUID id,
            @RequestBody RoleUpdateDTO request) {
        RoleResponseDTO response = roleService.updateRole(id, request);
        return ApiResponse.success(response, "Role updated successfully.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role", description = "Removes a role from the system.")
    public ApiResponse<Void> deleteRole(@PathVariable() UUID id) {
        roleService.deleteRole(id);
        return ApiResponse.success(null, "Role deleted successfully.");
    }
}