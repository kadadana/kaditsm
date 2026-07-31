package com.kadadana.kaditsm.modules.ticketstatus.controller;

//JAVA IMPORTS
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;

//CORE IMPORTS
import com.kadadana.kaditsm.core.model.ApiResponse;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.ticketstatus.dto.TicketStatusCreateDTO;
import com.kadadana.kaditsm.modules.ticketstatus.dto.TicketStatusResponseDTO;
import com.kadadana.kaditsm.modules.ticketstatus.dto.TicketStatusUpdateDTO;
import com.kadadana.kaditsm.modules.ticketstatus.service.TicketStatusService;

@RestController
@RequestMapping("/api/v1/statuses")
@RequiredArgsConstructor
@Tag(name = "Statuses", description = "Endpoints to manage ticket statuses (e.g., OPEN, IN_PROGRESS, CLOSED)")
public class TicketStatusController {

    private final TicketStatusService statusService;

    @GetMapping
    @Operation(summary = "List ticket statuses", description = "Retrieve a list of active ticket status types.")
    public ApiResponse<List<TicketStatusResponseDTO>> getAllStatuses() {
        List<TicketStatusResponseDTO> statuses = statusService.getAllActiveStatuses();
        return ApiResponse.success(statuses, "Ticket statuses retrieved successfully.");
    }

    @PostMapping
    @Operation(summary = "Create ticket status", description = "Create a new ticket status type used in ticket lifecycles.")
    public ApiResponse<TicketStatusResponseDTO> createStatus(@RequestBody TicketStatusCreateDTO createDTO) {
        TicketStatusResponseDTO createdStatus = statusService.createStatus(createDTO);
        return ApiResponse.success(createdStatus, "Ticket status created successfully.");
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update ticket status", description = "Update an existing ticket status type by ID.")
    public ApiResponse<TicketStatusResponseDTO> patchStatus(
            @PathVariable UUID id,
            @RequestBody TicketStatusUpdateDTO updateDTO) {
        TicketStatusResponseDTO updatedStatus = statusService.updateStatus(id, updateDTO);
        return ApiResponse.success(updatedStatus, "Ticket status updated successfully.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate ticket status", description = "Deactivate a ticket status type instead of deleting it permanently.")
    public ApiResponse<Void> deleteStatus(@PathVariable UUID id) {
        statusService.deactivateStatus(id);
        return ApiResponse.success(null, "Ticket status deactivated successfully.");
    }
}