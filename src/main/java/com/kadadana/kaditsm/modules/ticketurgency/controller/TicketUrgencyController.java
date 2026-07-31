package com.kadadana.kaditsm.modules.ticketurgency.controller;

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
import com.kadadana.kaditsm.modules.ticketurgency.dto.TicketUrgencyCreateDTO;
import com.kadadana.kaditsm.modules.ticketurgency.dto.TicketUrgencyResponseDTO;
import com.kadadana.kaditsm.modules.ticketurgency.dto.TicketUrgencyUpdateDTO;
import com.kadadana.kaditsm.modules.ticketurgency.service.TicketUrgencyService;

@RestController
@RequestMapping("/api/v1/urgencies")
@RequiredArgsConstructor
@Tag(name = "Urgencies", description = "Endpoints to manage ticket urgency levels")
public class TicketUrgencyController {

    private final TicketUrgencyService urgencyService;

    @GetMapping
    @Operation(summary = "List urgency levels", description = "Retrieve a list of active ticket urgency levels.")
    public ApiResponse<List<TicketUrgencyResponseDTO>> getAllUrgencies() {
        List<TicketUrgencyResponseDTO> urgencies = urgencyService.getAllActiveUrgencies();
        return ApiResponse.success(urgencies, "Urgencies retrieved successfully.");
    }

    @PostMapping
    @Operation(summary = "Create urgency level", description = "Create a new urgency level used to prioritize tickets.")
    public ApiResponse<TicketUrgencyResponseDTO> createUrgency(@RequestBody TicketUrgencyCreateDTO createDTO) {
        TicketUrgencyResponseDTO createdUrgency = urgencyService.createUrgency(createDTO);
        return ApiResponse.success(createdUrgency, "Urgency level created successfully.");
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update urgency level", description = "Update an existing urgency level by ID.")
    public ApiResponse<TicketUrgencyResponseDTO> patchUrgency(
            @PathVariable UUID id,
            @RequestBody TicketUrgencyUpdateDTO updateDTO) {
        TicketUrgencyResponseDTO updatedUrgency = urgencyService.editUrgency(id, updateDTO);
        return ApiResponse.success(updatedUrgency, "Urgency level updated successfully.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate urgency level", description = "Deactivate an urgency level instead of permanently deleting it.")
    public ApiResponse<Void> deleteUrgency(@PathVariable UUID id) {
        urgencyService.deactivateUrgency(id);
        return ApiResponse.success(null, "Urgency level deactivated successfully.");
    }
}