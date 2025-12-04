package org.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdempotencyRecord{
    @Id
    private String idempotencyKey;

    private String actionType;

    @Column(columnDefinition = "TEXT")
    private String requestJson; // JSON

    @Column(columnDefinition = "TEXT")
    private String responseJson; // JSON string

    private LocalDateTime createdAt;


    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

}
