package org.example.orderservice.service;

import lombok.AllArgsConstructor;
import org.example.orderservice.dtos.*;
import org.example.orderservice.entity.*;
import org.example.orderservice.exception.ResourceNotFoundException;
import org.example.orderservice.feign.ProductClient;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.repository.OutboxRepository;
import org.example.orderservice.security.CustomUserDetails;
import org.example.orderservice.utils.JsonUtils;
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
    public final ProductClient productClient;
    public final JsonUtils jsonUtils;
    private final OutboxRepository outboxRepository;

    @Transactional
    public OrderDTO prepareAndPersistOrder(List<ReserveProductDTO> reserveProducts, List<ReserveResponseDTO> productsData, String idempotencyKey) {
        for (ReserveResponseDTO reservedProduct : productsData) {
            for(ReserveProductDTO reservedProductFromInventory : reserveProducts) {
                if(reservedProduct.getProductId().equals(reservedProductFromInventory.getProductId())) {
                    reservedProduct.setQuantity(reservedProductFromInventory.getQuantity());
                    break;
                }
            }
        }
        List<OrderItem> orderItems = orderMapper.toOrderItems(productsData);

        Order save = createOrder(orderItems);

        OrderDTO dto = orderMapper.toDTO(save);

        ReserveForOrderDTO build = ReserveForOrderDTO.builder().reserveProducts(reserveProducts).orderId(save.getId()).build();
        Event event = Event.builder().eventType("RESERVE_PRODUCTS").payload(build).build();
        Outbox outbox = Outbox.builder().event(jsonUtils.toJson(event)).topicName("inventory-topic").eventType("RESERVE_PRODUCTS").build();
        outboxRepository.save(outbox);

        return dto;
    }

    private Order createOrder(List<OrderItem> orderItems) {
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

        return orderRepository.save(order);
    }

    @Transactional
    public List<ReserveProductDTO> getReserveProductDTOS(CancelOrderDTO orderDTO) {
        Order order = orderRepository.findById(orderDTO.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("ORDER NOT FOUND!"));
        if(order.getStatus().equals(OrderStatus.CANCELLED))
            return null;

        if(!order.getStatus().equals(OrderStatus.PENDING))
            throw new IllegalStateException("ORDER IS ALREADY PAID FOR!");

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        List<ReserveProductDTO> list = order.getOrderItems().stream().map(dto -> ReserveProductDTO.builder().productId(dto.getProductId()).quantity(dto.getQuantity()).build()).toList();
        return list;
    }

    @Transactional
    public void compensateGetReserveProductDTOS(CancelOrderDTO orderDTO) {
        Order order = orderRepository.findById(orderDTO.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("ORDER NOT FOUND!"));
        if(!order.getStatus().equals(OrderStatus.CANCELLED))
            throw new IllegalStateException("ORDER IS ALREADY PAID FOR!");

        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
    }
}
