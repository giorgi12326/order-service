package org.example.orderservice.dtos;

import lombok.Data;

@Data
public class ReserveResponseDTO {
    private Long productId;

    private String productName;

    private String productDescription;

    private Integer quantity;

    private Float price;
}
