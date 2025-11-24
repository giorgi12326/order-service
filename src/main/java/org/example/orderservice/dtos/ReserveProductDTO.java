package org.example.orderservice.dtos;

import lombok.Data;

@Data
public class ReserveProductDTO {
    Long productId;

    Integer quantity;
}
