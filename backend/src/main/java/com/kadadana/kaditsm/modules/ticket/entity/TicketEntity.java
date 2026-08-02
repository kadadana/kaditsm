package com.kadadana.kaditsm.modules.ticket.entity;

//JAVA IMPORTS
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "status_id", nullable = false)
    private UUID statusId;

    @Column(name = "urgency_id", nullable = false)
    private UUID urgencyId;

    @Column(name = "assigned_user_id")
    private UUID assignedUserId;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}