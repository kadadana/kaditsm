package com.kadadana.kaditsm.modules.ticket.controller;

import com.kadadana.kaditsm.core.model.ApiResponse;
import com.kadadana.kaditsm.modules.ticket.dto.TicketCreateDTO;
import com.kadadana.kaditsm.modules.ticket.dto.TicketResponseDTO;
import com.kadadana.kaditsm.modules.ticket.dto.TicketUpdateDTO;
import com.kadadana.kaditsm.modules.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public ApiResponse<List<TicketResponseDTO>> getAllTickets() {
        List<TicketResponseDTO> tickets = ticketService.getAllTickets();
        return ApiResponse.success(tickets, "Tickets listed successfully.");
    }

    @GetMapping("/{id}")
    public ApiResponse<TicketResponseDTO> getTicketById(@PathVariable UUID id) {
        TicketResponseDTO ticket = ticketService.getTicketById(id);
        return ApiResponse.success(ticket, "Ticket details retrieved successfully.");
    }

    @PostMapping
    public ApiResponse<TicketResponseDTO> createTicket(@RequestBody TicketCreateDTO createDTO) {
        TicketResponseDTO createdTicket = ticketService.createTicket(createDTO);
        return ApiResponse.success(createdTicket, "Ticket created successfully.");
    }

    @PatchMapping("/{id}")
    public ApiResponse<TicketResponseDTO> patchTicket(
            @PathVariable UUID id,
            @RequestBody TicketUpdateDTO updateDTO) {
        TicketResponseDTO updatedTicket = ticketService.patchTicket(id, updateDTO);
        return ApiResponse.success(updatedTicket, "Ticket updated successfully.");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
        return ApiResponse.success(null, "Ticket deleted successfully.");
    }
}