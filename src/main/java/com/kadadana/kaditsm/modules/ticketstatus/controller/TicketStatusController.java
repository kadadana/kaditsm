package com.kadadana.kaditsm.modules.ticketstatus.controller;

import com.kadadana.kaditsm.core.model.ApiResponse;
import com.kadadana.kaditsm.modules.ticketstatus.dto.TicketStatusCreateDTO;
import com.kadadana.kaditsm.modules.ticketstatus.dto.TicketStatusResponseDTO;
import com.kadadana.kaditsm.modules.ticketstatus.dto.TicketStatusUpdateDTO;
import com.kadadana.kaditsm.modules.ticketstatus.service.TicketStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/statuses")
@RequiredArgsConstructor
public class TicketStatusController {

    private final TicketStatusService statusService;

    @GetMapping
    public ApiResponse<List<TicketStatusResponseDTO>> getAllStatuses() {
        List<TicketStatusResponseDTO> statuses = statusService.getAllActiveStatuses();
        return ApiResponse.success(statuses, "Ticket statuses retrieved successfully.");
    }

    @PostMapping
    public ApiResponse<TicketStatusResponseDTO> createStatus(@RequestBody TicketStatusCreateDTO createDTO) {
        TicketStatusResponseDTO createdStatus = statusService.createStatus(createDTO);
        return ApiResponse.success(createdStatus, "Ticket status created successfully.");
    }

    @PatchMapping("/{id}")
    public ApiResponse<TicketStatusResponseDTO> patchStatus(
            @PathVariable UUID id,
            @RequestBody TicketStatusUpdateDTO updateDTO) {
        TicketStatusResponseDTO updatedStatus = statusService.patchStatus(id, updateDTO);
        return ApiResponse.success(updatedStatus, "Ticket status updated successfully.");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStatus(@PathVariable UUID id) {
        statusService.deleteStatus(id);
        return ApiResponse.success(null, "Ticket status deactivated successfully.");
    }
}