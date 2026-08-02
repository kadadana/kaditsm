package com.kadadana.kaditsm.modules.role.repository;

//JAVA IMPORTS
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.role.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findById(UUID id);

    Optional<RoleEntity> findByName(String name);

    boolean existsByName(String name);

    Optional<RoleEntity> findByIsDefaultTrue();
}