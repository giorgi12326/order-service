package org.example.orderservice.repository;

import org.example.orderservice.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotentRecordRepository extends JpaRepository<IdempotencyRecord, String> {
}
