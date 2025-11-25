package org.example.orderservice.dtos;

import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private Long userId;
    private Float amount;
    private String status;
    private String createdAt;
    private String updatedAt;
    private List<OrderItemDTO> orderItems;
}