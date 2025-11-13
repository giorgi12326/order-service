package org.example.orderservice.service;

import lombok.AllArgsConstructor;
import org.example.orderservice.entity.Order;
import org.example.orderservice.feign.ProductClient;
import org.example.orderservice.feign.UserClient;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    public final OrderRepository orderRepository;
    public final UserClient userClient;
    public final ProductClient productClient;

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Order createOrder(Order order) {
        if(userClient.userExists(order.getUsername()) && productClient.existsById(order.getProductId())) {
            return orderRepository.save(order);
        }
        throw new RuntimeException("User does not exist");
    }
}
