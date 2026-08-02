package com.kadadana.kaditsm.modules.ticketcategory.repository;

//JAVA IMPORTS
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.ticketcategory.entity.TicketCategoryEntity;

public interface TicketCategoryRepository extends JpaRepository<TicketCategoryEntity, UUID> {

    List<TicketCategoryEntity> findAllByActiveTrue();
}