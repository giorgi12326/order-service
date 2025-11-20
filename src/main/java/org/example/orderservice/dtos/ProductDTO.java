package org.example.orderservice.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    @NotNull
    String name;

    String description;

    LocalDate createdAt;

    Float price;

}
