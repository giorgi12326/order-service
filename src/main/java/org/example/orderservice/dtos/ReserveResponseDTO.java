package org.example.orderservice.dtos;

import lombok.Data;

@Data
public class ReserveResponseDTO {
    Long id;

    Integer quantity;

    Float price;
}
