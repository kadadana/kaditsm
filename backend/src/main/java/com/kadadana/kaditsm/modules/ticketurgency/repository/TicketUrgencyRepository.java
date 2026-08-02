package com.kadadana.kaditsm.modules.ticketurgency.repository;

//JAVA IMPORTS
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.ticketurgency.entity.TicketUrgencyEntity;

public interface TicketUrgencyRepository extends JpaRepository<TicketUrgencyEntity, UUID> {

    List<TicketUrgencyEntity> findAllByActiveTrue();

    Optional<TicketUrgencyEntity> findByCode(String code);
}