package org.example.orderservice.service;

import lombok.AllArgsConstructor;
import org.example.orderservice.dtos.OrderDTO;
import org.example.orderservice.dtos.ProductDTO;
import org.example.orderservice.entity.Order;
import org.example.orderservice.exception.ResourceNotFoundException;
import org.example.orderservice.feign.ProductClient;
import org.example.orderservice.feign.UserClient;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    public final OrderRepository orderRepository;
    public final UserClient userClient;
    public final ProductClient productClient;
    public final OrderMapper orderMapper;
    private CacheManager cacheManager;

    @Cacheable(value = "order-cache")
    public List<Order> getAll() {
        System.out.println("Getting all orders From Database!");
        System.out.println(cacheManager.getCache("order-cache"));

        return orderRepository.findAll();
    }

    @CacheEvict(value = "order-cache", allEntries = true)
    public Order createOrder(Order order) {
        System.out.println(cacheManager.getCache("order-cache"));

        if(userClient.userExists(order.getUserId()) && productClient.existsById(order.getProductId())) {
            return orderRepository.save(order);
        }
        throw new RuntimeException("User does not exist");
    }

    @CacheEvict(value = "order-cache", allEntries = true)
    public OrderDTO update(OrderDTO order, Long id) {
        System.out.println(cacheManager.getCache("order-cache"));
        Order updatedOrder = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if(!productClient.existsById(order.getProductId()))
            throw new ResourceNotFoundException("Product not found");
        if(!userClient.userExists(order.getUserId()))
            throw new ResourceNotFoundException("User does not exist");

        updatedOrder = orderMapper.updateEntity(order, updatedOrder);
        Order save = orderRepository.save(updatedOrder);
        return orderMapper.toDTO(save);
    }
}
