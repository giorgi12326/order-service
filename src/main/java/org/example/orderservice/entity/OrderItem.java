package org.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "orders")
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;

    Long productId;

    Integer quantity;

    Float price;
}