package org.example.orderservice.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReserveProductDTO {
    Long productId;

    Integer quantity;
}
