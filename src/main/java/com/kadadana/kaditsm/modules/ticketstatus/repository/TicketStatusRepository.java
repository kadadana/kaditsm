package com.kadadana.kaditsm.modules.ticketstatus.repository;

import com.kadadana.kaditsm.modules.ticketstatus.entity.TicketStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketStatusRepository extends JpaRepository<TicketStatusEntity, UUID> {

    List<TicketStatusEntity> findAllByActiveTrue();

    Optional<TicketStatusEntity> findByIsDefaultTrueAndActiveTrue();

    Optional<TicketStatusEntity> findByIsDefaultTrue();
}