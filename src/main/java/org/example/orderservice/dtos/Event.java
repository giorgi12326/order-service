package org.example.orderservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private EventType eventType;
    private Instant timestamp;
    private Object payload;
}
