package org.example.orderservice.service;

import lombok.AllArgsConstructor;
import org.example.orderservice.dtos.ReserveProductDTO;
import org.example.orderservice.dtos.ReserveResponseDTO;
import org.example.orderservice.entity.Order;
import org.example.orderservice.feign.ProductClient;
import org.example.orderservice.feign.UserClient;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.repository.OrderRepository;
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
        if(!userClient.userExists(order.getUserId()) ) {
            throw new RuntimeException("User does not exist");
        }

        List<ReserveProductDTO> orderItems = orderMapper.toReserveDTO(order.getOrderItems());
        List<ReserveResponseDTO> productsByID = productClient.getAndReserveProducts(orderItems);

        float amount = 0;
        for (ReserveResponseDTO responseDTO : productsByID) {
            amount += responseDTO.getPrice() * responseDTO.getQuantity();
        }
        order.setAmount(amount);

        return orderRepository.save(order);

    }

//    @CacheEvict(value = "order-cache", allEntries = true)
//    public OrderDTO update(OrderDTO order, Long id) {
//        System.out.println(cacheManager.getCache("order-cache"));
//        Order updatedOrder = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
//        if(!productClient.existsById(order.getProductId()))
//            throw new ResourceNotFoundException("Product not found");
//        if(!userClient.userExists(order.getUserId()))
//            throw new ResourceNotFoundException("User does not exist");
//
//        updatedOrder = orderMapper.updateEntity(order, updatedOrder);
//        Order save = orderRepository.save(updatedOrder);
//        return orderMapper.toDTO(save);
//    }
}
