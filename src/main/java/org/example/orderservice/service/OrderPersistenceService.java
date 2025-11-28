package org.example.orderservice.service;

import lombok.AllArgsConstructor;
import org.example.orderservice.dtos.OrderDTO;
import org.example.orderservice.dtos.ReserveResponseDTO;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderItem;
import org.example.orderservice.entity.OrderStatus;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.security.CustomUserDetails;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderPersistenceService {
    public final OrderMapper orderMapper;
    public final OrderRepository orderRepository;


    @Transactional
    public OrderDTO getOrderDTO(List<ReserveResponseDTO> reservedProducts) {
        System.out.println(reservedProducts);
        List<OrderItem> orderItems = orderMapper.toOrderItems(reservedProducts);

        Order order = new Order();

        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) user.getPrincipal();
        order.setUserId(principal.getId());
        order.setStatus(OrderStatus.PENDING);

        float amount = 0f;
        for (OrderItem item : orderItems) {
            amount += item.getPrice() * item.getQuantity();
            item.setOrder(order);
        }

        order.setOrderItems(orderItems);
        order.setAmount(amount);

        return orderMapper.toDTO(orderRepository.save(order));
    }
}
