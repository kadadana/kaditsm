package com.kadadana.kaditsm.modules.ticketurgency.service;

import com.kadadana.kaditsm.modules.ticketurgency.dto.TicketUrgencyCreateDTO;
import com.kadadana.kaditsm.modules.ticketurgency.dto.TicketUrgencyResponseDTO;
import com.kadadana.kaditsm.modules.ticketurgency.dto.TicketUrgencyUpdateDTO;
import com.kadadana.kaditsm.modules.ticketurgency.entity.TicketUrgencyEntity;
import com.kadadana.kaditsm.modules.ticketurgency.mapper.TicketUrgencyMapper;
import com.kadadana.kaditsm.modules.ticketurgency.repository.TicketUrgencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketUrgencyService {

    private final TicketUrgencyRepository urgencyRepository;
    private final TicketUrgencyMapper urgencyMapper;

    @Transactional(readOnly = true)
    public List<TicketUrgencyResponseDTO> getAllActiveUrgencies() {
        return urgencyRepository.findAllByActiveTrue().stream()
                .map(urgencyMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public TicketUrgencyResponseDTO createUrgency(TicketUrgencyCreateDTO createDTO) {
        TicketUrgencyEntity entity = urgencyMapper.toEntity(createDTO);
        TicketUrgencyEntity savedEntity = urgencyRepository.save(entity);
        return urgencyMapper.toResponseDTO(savedEntity);
    }

    @Transactional
    public TicketUrgencyResponseDTO patchUrgency(UUID id, TicketUrgencyUpdateDTO updateDTO) {
        TicketUrgencyEntity entity = urgencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Urgency level not found: " + id));

        urgencyMapper.updateEntityFromDto(updateDTO, entity);
        TicketUrgencyEntity updatedEntity = urgencyRepository.save(entity);
        return urgencyMapper.toResponseDTO(updatedEntity);
    }

    @Transactional
    public void deleteUrgency(UUID id) {
        TicketUrgencyEntity entity = urgencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Urgency level not found: " + id));

        entity.setActive(false);
        urgencyRepository.save(entity);
    }
}