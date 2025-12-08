package org.example.orderservice.repository;

import org.example.orderservice.entity.Outbox;
import org.example.orderservice.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {
    List<Outbox> findOutboxesByStatus(OutboxStatus status);
}
