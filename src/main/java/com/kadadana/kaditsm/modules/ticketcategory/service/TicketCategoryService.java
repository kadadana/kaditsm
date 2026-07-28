package com.kadadana.kaditsm.modules.ticketcategory.service;

import com.kadadana.kaditsm.modules.ticketcategory.dto.TicketCategoryCreateDTO;
import com.kadadana.kaditsm.modules.ticketcategory.dto.TicketCategoryResponseDTO;
import com.kadadana.kaditsm.modules.ticketcategory.dto.TicketCategoryUpdateDTO;
import com.kadadana.kaditsm.modules.ticketcategory.entity.TicketCategoryEntity;
import com.kadadana.kaditsm.modules.ticketcategory.mapper.TicketCategoryMapper;
import com.kadadana.kaditsm.modules.ticketcategory.repository.TicketCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketCategoryService {

    private final TicketCategoryRepository categoryRepository;
    private final TicketCategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<TicketCategoryResponseDTO> getAllActiveCategories() {
        return categoryRepository.findAllByActiveTrue().stream()
                .map(categoryMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public TicketCategoryResponseDTO createCategory(TicketCategoryCreateDTO createDTO) {
        TicketCategoryEntity entity = categoryMapper.toEntity(createDTO);
        TicketCategoryEntity savedEntity = categoryRepository.save(entity);
        return categoryMapper.toResponseDTO(savedEntity);
    }

    @Transactional
    public TicketCategoryResponseDTO updateCategory(UUID id, TicketCategoryUpdateDTO updateDTO) {
        TicketCategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));

        categoryMapper.updateEntityFromDto(updateDTO, entity);
        TicketCategoryEntity updatedEntity = categoryRepository.save(entity);
        return categoryMapper.toResponseDTO(updatedEntity);
    }

    @Transactional
    public void deactivateCategory(UUID id) {
        TicketCategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));

        entity.setActive(false);
        categoryRepository.save(entity);
    }
}