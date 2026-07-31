package com.kadadana.kaditsm.modules.ticketurgency.entity;

//JAVA IMPORTS
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "ticket_urgencies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketUrgencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String label;

    @Column(length = 7)
    private String colorCode;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

}