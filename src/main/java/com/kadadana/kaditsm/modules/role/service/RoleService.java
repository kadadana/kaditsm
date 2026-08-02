package com.kadadana.kaditsm.modules.role.service;

//JAVA IMPORTS
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.role.dto.RoleCreateDTO;
import com.kadadana.kaditsm.modules.role.dto.RoleResponseDTO;
import com.kadadana.kaditsm.modules.role.dto.RoleUpdateDTO;
import com.kadadana.kaditsm.modules.role.entity.RoleEntity;
import com.kadadana.kaditsm.modules.role.mapper.RoleMapper;
import com.kadadana.kaditsm.modules.role.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleResponseDTO getRoleById(UUID id) {
        RoleEntity entity = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found: " + id));
        return roleMapper.toResponseDTO(entity);
    }

    @Transactional
    public RoleResponseDTO updateRole(UUID id, RoleUpdateDTO updateDTO) {
        RoleEntity entity = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found: " + id));

        if (Boolean.TRUE.equals(updateDTO.getIsDefault())) {
            clearPreviousDefaultRole();
        }

        roleMapper.updateEntityFromDto(updateDTO, entity);
        RoleEntity updatedEntity = roleRepository.save(entity);
        return roleMapper.toResponseDTO(updatedEntity);
    }

    @Transactional
    public RoleResponseDTO createRole(RoleCreateDTO createDTO) {
        if (Boolean.TRUE.equals(createDTO.getIsDefault())) {
            clearPreviousDefaultRole();
        }

        RoleEntity entity = roleMapper.toEntity(createDTO);
        RoleEntity savedEntity = roleRepository.save(entity);
        return roleMapper.toResponseDTO(savedEntity);
    }

    @Transactional
    public void deleteRole(UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found: " + id);
        }
        roleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public RoleResponseDTO getDefaultRole() {
        RoleEntity entity = roleRepository.findByIsDefaultTrue()
                .orElseThrow(() -> new RuntimeException("Default role not found."));
        return roleMapper.toResponseDTO(entity);
    }

    private void clearPreviousDefaultRole() {
        roleRepository.findByIsDefaultTrue()
                .ifPresent(role -> {
                    role.setIsDefault(false);
                    roleRepository.save(role);
                });
    }
}