package com.kadadana.kaditsm.modules.ticket.controller;

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
import com.kadadana.kaditsm.modules.ticket.dto.TicketCreateDTO;
import com.kadadana.kaditsm.modules.ticket.dto.TicketResponseDTO;
import com.kadadana.kaditsm.modules.ticket.dto.TicketUpdateDTO;
import com.kadadana.kaditsm.modules.ticket.service.TicketService;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Endpoints to manage helpdesk tickets")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    @Operation(summary = "List tickets", description = "Retrieve a list of all helpdesk tickets.")
    public ApiResponse<List<TicketResponseDTO>> getAllTickets() {
        List<TicketResponseDTO> tickets = ticketService.getAllTickets();
        return ApiResponse.success(tickets, "Tickets listed successfully.");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by ID", description = "Retrieve ticket details for the given ticket ID.")
    public ApiResponse<TicketResponseDTO> getTicketById(@PathVariable UUID id) {
        TicketResponseDTO ticket = ticketService.getTicketById(id);
        return ApiResponse.success(ticket, "Ticket details retrieved successfully.");
    }

    @PostMapping
    @Operation(summary = "Create ticket", description = "Create a new helpdesk ticket with the provided details.")
    public ApiResponse<TicketResponseDTO> createTicket(@RequestBody TicketCreateDTO createDTO) {
        TicketResponseDTO createdTicket = ticketService.createTicket(createDTO);
        return ApiResponse.success(createdTicket, "Ticket created successfully.");
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update ticket", description = "Partially update ticket fields for the given ID.")
    public ApiResponse<TicketResponseDTO> patchTicket(
            @PathVariable UUID id,
            @RequestBody TicketUpdateDTO updateDTO) {
        TicketResponseDTO updatedTicket = ticketService.patchTicket(id, updateDTO);
        return ApiResponse.success(updatedTicket, "Ticket updated successfully.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete ticket", description = "Remove the ticket with the specified ID.")
    public ApiResponse<Void> deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
        return ApiResponse.success(null, "Ticket deleted successfully.");
    }
}