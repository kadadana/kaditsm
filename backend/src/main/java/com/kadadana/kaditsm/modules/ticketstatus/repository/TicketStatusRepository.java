package com.kadadana.kaditsm.modules.ticketstatus.repository;

//JAVA IMPORTS
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.ticketstatus.entity.TicketStatusEntity;

public interface TicketStatusRepository extends JpaRepository<TicketStatusEntity, UUID> {

    List<TicketStatusEntity> findAllByActiveTrue();

    Optional<TicketStatusEntity> findByIsDefaultTrueAndActiveTrue();

    Optional<TicketStatusEntity> findByIsDefaultTrue();
}