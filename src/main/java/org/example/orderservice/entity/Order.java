package org.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long userId;

    Float amount;

    OrderStatus status;

    LocalDateTime creationDate;

    LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.ALL)
    List<OrderItem> orderItems;

    @PrePersist
    public void prePersist() {
        creationDate = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
