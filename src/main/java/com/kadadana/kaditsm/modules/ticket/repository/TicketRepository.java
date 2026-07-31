package com.kadadana.kaditsm.modules.ticket.repository;

//JAVA IMPORTS
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.ticket.entity.TicketEntity;

public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {
}