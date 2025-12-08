package org.example.orderservice.scheduler;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.orderservice.dtos.Event;
import org.example.orderservice.entity.Outbox;
import org.example.orderservice.entity.OutboxStatus;
import org.example.orderservice.repository.OutboxRepository;
import org.example.orderservice.service.OrderPersistenceService;
import org.example.orderservice.utils.JsonUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class OutboxScheduler {

    public final JsonUtils jsonUtils;

    public final OutboxRepository outboxRepository;

    private final KafkaTemplate<String, Event> kafkaTemplate;
    private final OrderPersistenceService orderPersistenceService;

    @Scheduled(fixedRate = 10000)
    public void publishPendingOutbox() {
        List<Outbox> pendingOutboxes = outboxRepository.findOutboxesByStatus(OutboxStatus.PENDING);
        pendingOutboxes.forEach((outbox) -> {
            Event event = jsonUtils.fromJson(outbox.getEvent(), Event.class);
            kafkaTemplate.send(outbox.getTopicName(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.out.println("sending Failed on attempt: " + outbox.getAttempts());
                        orderPersistenceService.incrementAttempts(outbox);
                        if (outbox.getAttempts() >= 5) {
                            orderPersistenceService.markAsFailed(outbox);
                        }
                    } else {
                        orderPersistenceService.markAsSucceeded(outbox);
                        System.out.println("sending Completed!");
                    }
                });
        });
    }


}