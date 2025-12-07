package org.example.orderservice.service;

import lombok.AllArgsConstructor;
import org.example.orderservice.dtos.Event;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderStatus;
import org.example.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "user-event", groupId = "my-group")
    @Transactional
    public void userListener(Event event) {
        log.info("Received user-event: {}", event.toString());
        if (event.getEventType().equals("DELETED")) {
            List<Order> allByUserId = orderRepository.findAllByUserId(((Integer) event.getPayload()).longValue());
            allByUserId.forEach(order -> {order.setStatus(OrderStatus.ORPHANED);});
            log.info("Orphaned order By UserId: {}", event.getPayload());
        }
    }
    @KafkaListener(topics = "order-topic", groupId = "my-group")
    @Transactional
    public void orderListener(Event event) {
        if(event.getEventType().equals("PRODUCTS_RESERVED")) {
            Long id = (Long) event.getPayload();
            orderRepository.findById(id)
                .ifPresent(order -> {
                    order.setStatus(OrderStatus.CREATED_NEEDS_PAYMENT);
                    orderRepository.save(order);
                });
        }
    }

}