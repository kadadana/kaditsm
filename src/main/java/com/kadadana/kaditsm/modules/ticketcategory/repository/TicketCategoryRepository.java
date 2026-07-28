package com.kadadana.kaditsm.modules.ticketcategory.repository;

import com.kadadana.kaditsm.modules.ticketcategory.entity.TicketCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketCategoryRepository extends JpaRepository<TicketCategoryEntity, UUID> {

    List<TicketCategoryEntity> findAllByActiveTrue();
}