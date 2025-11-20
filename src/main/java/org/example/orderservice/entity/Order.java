package org.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long userId;

    @Column(nullable = false)
    List<Long> productIds;

    float totalPrice;

    @Enumerated(EnumType.STRING)
    OrderStatus status;
}