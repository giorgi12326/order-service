package org.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "order_items")
@Setter
@Getter
@ToString(exclude = "order")
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;

    Long productId;

    String productName;

    String productDescription;

    Integer quantity;

    Float price;
}