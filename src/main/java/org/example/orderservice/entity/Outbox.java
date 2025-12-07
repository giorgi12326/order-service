package org.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String eventType;

    String event;

    String topicName;

    Integer attempts = 0;

    @Enumerated(EnumType.STRING)
    OutboxStatus status =  OutboxStatus.PENDING;

}