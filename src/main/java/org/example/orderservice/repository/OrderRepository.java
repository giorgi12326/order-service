package org.example.orderservice.repository;

import org.example.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
//    void deleteByProductId(Long productId);

    void deleteByUserId(long userId);

    List<Order> findAllByUserId(Long userId);
}
