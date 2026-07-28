package com.kadadana.kaditsm.modules.auth.repository;

import com.kadadana.kaditsm.modules.auth.entity.AuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthRepository extends JpaRepository<AuthEntity, UUID> {
}