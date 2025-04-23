package com.sokoplace.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Data // Lombok annotation: generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincrement
    private Long Id;

    @Column(name = "sku", nullable = false, length = 20)
    private String sku;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist // JPA callback: executed before entity is persisted
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
