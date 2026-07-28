package com.kadadana.kaditsm.modules.ticketstatus.service;

import com.kadadana.kaditsm.modules.ticketstatus.dto.TicketStatusCreateDTO;
import com.kadadana.kaditsm.modules.ticketstatus.dto.TicketStatusResponseDTO;
import com.kadadana.kaditsm.modules.ticketstatus.dto.TicketStatusUpdateDTO;
import com.kadadana.kaditsm.modules.ticketstatus.entity.TicketStatusEntity;
import com.kadadana.kaditsm.modules.ticketstatus.mapper.TicketStatusMapper;
import com.kadadana.kaditsm.modules.ticketstatus.repository.TicketStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketStatusService {

    private final TicketStatusRepository statusRepository;
    private final TicketStatusMapper statusMapper;

    @Transactional(readOnly = true)
    public List<TicketStatusResponseDTO> getAllActiveStatuses() {
        return statusRepository.findAllByActiveTrue().stream()
                .map(statusMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public TicketStatusResponseDTO createStatus(TicketStatusCreateDTO createDTO) {
        if (Boolean.TRUE.equals(createDTO.getIsDefault())) {
            clearPreviousDefaultStatus();
        }

        TicketStatusEntity entity = statusMapper.toEntity(createDTO);
        TicketStatusEntity savedEntity = statusRepository.save(entity);
        return statusMapper.toResponseDTO(savedEntity);
    }

    @Transactional
    public TicketStatusResponseDTO patchStatus(UUID id, TicketStatusUpdateDTO updateDTO) {
        TicketStatusEntity entity = statusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket status not found: " + id));

        if (Boolean.TRUE.equals(updateDTO.getIsDefault())) {
            clearPreviousDefaultStatus();
        }

        statusMapper.updateEntityFromDto(updateDTO, entity);
        TicketStatusEntity updatedEntity = statusRepository.save(entity);
        return statusMapper.toResponseDTO(updatedEntity);
    }

    @Transactional
    public void deleteStatus(UUID id) {
        TicketStatusEntity entity = statusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket status not found: " + id));

        entity.setActive(false);
        statusRepository.save(entity);
    }

    private void clearPreviousDefaultStatus() {
        statusRepository.findByIsDefaultTrueAndActiveTrue()
                .ifPresent(status -> {
                    status.setIsDefault(false);
                    statusRepository.save(status);
                });
    }

    @Transactional(readOnly = true)
    public TicketStatusResponseDTO getDefaultStatus() {
        TicketStatusEntity entity = statusRepository.findByIsDefaultTrueAndActiveTrue()
                .orElseThrow(() -> new RuntimeException("Default status not found."));
        return statusMapper.toResponseDTO(entity);
    }
}