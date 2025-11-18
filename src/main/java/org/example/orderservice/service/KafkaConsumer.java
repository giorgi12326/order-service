package org.example.orderservice.service;

import lombok.AllArgsConstructor;
import org.example.orderservice.dtos.Event;
import org.example.orderservice.dtos.EventType;
import org.example.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "test-topic", groupId = "my-group")
    public void test(String message) {
        log.info("Received message: {}", message);
    }


    @KafkaListener(topics = "product-event", groupId = "my-group")
    @Transactional
    public void productListener(Event event) {
        log.info("Received product-event: {}", event.toString());
        if (event.getEventType() == EventType.DELETED) {
            orderRepository.deleteByProductId(((Integer)event.getPayload()).longValue());
            log.info("Deleted order By ProductId: {}", event.getPayload());
        }
    }

    @KafkaListener(topics = "user-event", groupId = "my-group")
    @Transactional
    public void userListener(Event event) {
        log.info("Received user-event: {}", event.toString());
        if (event.getEventType() == EventType.DELETED) {
            orderRepository.deleteByUserId(((Integer)event.getPayload()).longValue());
            log.info("Deleted order By UserId: {}", event.getPayload());
        }
    }


}