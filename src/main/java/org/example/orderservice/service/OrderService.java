package org.example.orderservice.service;

import lombok.AllArgsConstructor;
import org.example.orderservice.dtos.OrderDTO;
import org.example.orderservice.entity.Order;
import org.example.orderservice.exception.ResourceNotFoundException;
import org.example.orderservice.feign.ProductClient;
import org.example.orderservice.feign.UserClient;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    public final OrderRepository orderRepository;
    public final UserClient userClient;
    public final ProductClient productClient;
    public final OrderMapper orderMapper;


    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Order createOrder(Order order) {
        if(userClient.userExists(order.getUserId()) && productClient.existsById(order.getProductId())) {
            return orderRepository.save(order);
        }
        throw new RuntimeException("User does not exist");
    }

    public OrderDTO update(OrderDTO order, Long id) {
        Order updatedOrder = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        updatedOrder = orderMapper.updateEntity(order, updatedOrder);
        Order save = orderRepository.save(updatedOrder);
        return orderMapper.toDTO(save);
    }
}
