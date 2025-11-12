package org.example.orderservice.service;

import lombok.AllArgsConstructor;
import org.example.orderservice.entity.Order;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    public final OrderRepository orderRepository;

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Order createOrder(Order order) {

        return orderRepository.save(order);
    }
}
