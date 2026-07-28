package com.kadadana.kaditsm.modules.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kadadana.kaditsm.modules.user.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> findAllByRole(String role);
}