package org.example.orderservice.dtos;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long id;//dont send unless on update !!!
    private Long productId;
    private String productName;
    private String productDescription;
    private Integer quantity;
    private Float price;
}