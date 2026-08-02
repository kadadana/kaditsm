package com.kadadana.kaditsm.modules.ticket.service;

//JAVA IMPORTS
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.ticket.dto.TicketCreateDTO;
import com.kadadana.kaditsm.modules.ticket.dto.TicketResponseDTO;
import com.kadadana.kaditsm.modules.ticket.dto.TicketUpdateDTO;
import com.kadadana.kaditsm.modules.ticket.entity.TicketEntity;
import com.kadadana.kaditsm.modules.ticket.mapper.TicketMapper;
import com.kadadana.kaditsm.modules.ticket.repository.TicketRepository;

//OUTER IMPORTS
import com.kadadana.kaditsm.modules.ticketstatus.service.TicketStatusService;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketStatusService statusService;
    private final TicketMapper ticketMapper;

    @Transactional(readOnly = true)
    public List<TicketResponseDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(ticketMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TicketResponseDTO getTicketById(UUID id) {
        TicketEntity entity = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + id));
        return ticketMapper.toResponseDTO(entity);
    }

    @Transactional
    public TicketResponseDTO createTicket(TicketCreateDTO createDTO) {
        UUID defaultStatusId = statusService.getDefaultStatus().getId();

        TicketEntity entity = TicketEntity.builder()
                .title(createDTO.getTitle())
                .description(createDTO.getDescription())
                .categoryId(createDTO.getCategoryId())
                .urgencyId(createDTO.getUrgencyId())
                .statusId(defaultStatusId)
                .build();

        TicketEntity savedTicket = ticketRepository.save(entity);
        return ticketMapper.toResponseDTO(savedTicket);
    }

    @Transactional
    public TicketResponseDTO patchTicket(UUID id, TicketUpdateDTO updateDTO) {
        TicketEntity entity = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + id));

        ticketMapper.updateEntityFromDto(updateDTO, entity);

        TicketEntity updatedTicket = ticketRepository.save(entity);
        return ticketMapper.toResponseDTO(updatedTicket);
    }

    @Transactional
    public void deleteTicket(UUID id) {
        TicketEntity entity = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + id));

        entity.setActive(false);
        ticketRepository.save(entity);
    }

}