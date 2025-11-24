package org.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
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

    LocalDate createdAt;

    LocalDate updatedAt;

    @OneToMany(cascade = CascadeType.ALL)
    List<OrderItem> orderItems;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDate.now();
    }
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDate.now();
    }
}
