package org.example.orderservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderDTO {
    Long userId;
    List<Long> productId;
}
