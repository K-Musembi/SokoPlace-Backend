package com.sokoplace.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;  // JPA annotations: Entity, Table, Id, etc.
import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@Data  // Lombok annotation: generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id  // Set the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Autoincrement
    private Long Id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist  // JPA callback: executed before entity is persisted
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate  // JPA callback: executed before entity is updated
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

// Other JPA callbacks: @PostPersist, @PostUpdate, @PreRemove, @PostRemove