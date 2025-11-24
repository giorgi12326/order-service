package org.example.orderservice.dtos;

import lombok.Data;

@Data
public class ReserveResponseDTO {
    Long productId;

    Integer quantity;

    Float price;
}
