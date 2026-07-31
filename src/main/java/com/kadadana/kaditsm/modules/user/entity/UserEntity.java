package com.kadadana.kaditsm.modules.user.entity;

//JAVA IMPORTS
import java.util.UUID;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "department")
    private String department;

    @Column(name = "email")
    private String email;

    @Column(name = "role", nullable = false, length = 100)
    private String role;

}