package com.kadadana.kaditsm.modules.ticket.repository;

import com.kadadana.kaditsm.modules.ticket.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {
}