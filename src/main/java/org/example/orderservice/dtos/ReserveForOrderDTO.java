package org.example.orderservice.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class ReserveForOrderDTO {
    Long orderId;
    List<ReserveProductDTO> reserveProducts;
}
