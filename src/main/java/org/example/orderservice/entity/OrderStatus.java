package org.example.orderservice.entity;

public enum OrderStatus {
    PENDING,
    CREATED_NEEDS_PAYMENT,
    PAID,
    NOT_ENOUGH_IN_STOCK,
    SHIPPING,
    DELIVERED,
    CANCELLED,
    ORPHANED
}
