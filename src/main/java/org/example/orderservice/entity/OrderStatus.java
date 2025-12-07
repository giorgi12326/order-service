package org.example.orderservice.entity;

public enum OrderStatus {
    PENDING,
    CREATED_NEEDS_PAYMENT,
    PAID,
    SHIPPING,
    DELIVERED,
    CANCELLED,
    ORPHANED
}
