package com.kadadana.kaditsm.modules.ticketurgency.controller;

import com.kadadana.kaditsm.core.model.ApiResponse;
import com.kadadana.kaditsm.modules.ticketurgency.dto.TicketUrgencyCreateDTO;
import com.kadadana.kaditsm.modules.ticketurgency.dto.TicketUrgencyResponseDTO;
import com.kadadana.kaditsm.modules.ticketurgency.dto.TicketUrgencyUpdateDTO;
import com.kadadana.kaditsm.modules.ticketurgency.service.TicketUrgencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/urgencies")
@RequiredArgsConstructor
public class TicketUrgencyController {

    private final TicketUrgencyService urgencyService;

    @GetMapping
    public ApiResponse<List<TicketUrgencyResponseDTO>> getAllUrgencies() {
        List<TicketUrgencyResponseDTO> urgencies = urgencyService.getAllActiveUrgencies();
        return ApiResponse.success(urgencies, "Urgencies retrieved successfully.");
    }

    @PostMapping
    public ApiResponse<TicketUrgencyResponseDTO> createUrgency(@RequestBody TicketUrgencyCreateDTO createDTO) {
        TicketUrgencyResponseDTO createdUrgency = urgencyService.createUrgency(createDTO);
        return ApiResponse.success(createdUrgency, "Urgency level created successfully.");
    }

    @PatchMapping("/{id}")
    public ApiResponse<TicketUrgencyResponseDTO> patchUrgency(
            @PathVariable UUID id,
            @RequestBody TicketUrgencyUpdateDTO updateDTO) {
        TicketUrgencyResponseDTO updatedUrgency = urgencyService.editUrgency(id, updateDTO);
        return ApiResponse.success(updatedUrgency, "Urgency level updated successfully.");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUrgency(@PathVariable UUID id) {
        urgencyService.deactivateUrgency(id);
        return ApiResponse.success(null, "Urgency level deactivated successfully.");
    }
}