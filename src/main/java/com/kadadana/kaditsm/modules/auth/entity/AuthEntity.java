package com.kadadana.kaditsm.modules.auth.entity;

//JAVA IMPORTS
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "auths")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(nullable = false)
    private String password;
}