package org.example.orderservice.dtos;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
@Data
@Builder
public class ErrorMessageDTO {
    String message;
    HttpStatus status;
    LocalDateTime timestamp;
}
