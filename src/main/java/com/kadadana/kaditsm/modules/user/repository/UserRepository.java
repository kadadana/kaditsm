package com.kadadana.kaditsm.modules.user.repository;

//JAVA IMPORTS
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> findAllByRole(String role);
}