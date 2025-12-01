package org.example.orderservice.service;

import lombok.AllArgsConstructor;
import org.example.orderservice.dtos.CancelOrderDTO;
import org.example.orderservice.dtos.OrderDTO;
import org.example.orderservice.dtos.ReserveProductDTO;
import org.example.orderservice.dtos.ReserveResponseDTO;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderStatus;
import org.example.orderservice.exception.ConflictException;
import org.example.orderservice.exception.ResourceNotFoundException;
import org.example.orderservice.feign.InventoryClient;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    public final OrderRepository orderRepository;
    public final InventoryClient inventoryClient;
    public final OrderPersistenceService orderPersistenceService;
    public final OrderMapper orderMapper;

    public List<OrderDTO> getAll() {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Order> all = orderRepository.findAllByUserId(principal.getId());
        return orderMapper.toDTOs(all);
    }

    public OrderDTO createOrder(OrderDTO orderDTO) {
        List<ReserveProductDTO> reserveDTOs = orderMapper.toReserves(orderDTO.getOrderItems());
        List<ReserveProductDTO> reservedProducts;
        try {//here default to compensating , because i assumed that B commits more often then not
            reservedProducts = inventoryClient.getAndReserveProducts(reserveDTOs);
        }
        catch (feign.RetryableException e){
            inventoryClient.compensateReserveProducts(reserveDTOs);
            throw new RuntimeException("Order creation response lost!", e);
        }
        try{
            return orderPersistenceService.getOrderDTO(reservedProducts);
        }
        catch (Exception e){
            inventoryClient.compensateReserveProducts(reserveDTOs);
            throw new RuntimeException("Order creation response lost!", e);
        }
    }

    @Transactional
    public void cancelOrder(CancelOrderDTO orderDTO) {
        Order order = orderRepository.findById(orderDTO.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("ORDER NOT FOUND!"));
        orderRepository.delete(order);
        List<ReserveProductDTO> list = order.getOrderItems().stream().map(dto -> ReserveProductDTO.builder().productId(dto.getProductId()).quantity(dto.getQuantity()).build()).toList();
        inventoryClient.compensateReserveProducts(list);
    }

    @Transactional
    public OrderDTO payForOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order Not Found!"));
        if(order.getStatus().equals(OrderStatus.PAID)){
            throw new ConflictException("Order Payment Already made!");
        }
        order.setStatus(OrderStatus.PAID);
        return orderMapper.toDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderDTO unpayForOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order Not Found!"));
        if(order.getStatus().equals(OrderStatus.PAID)){
            order.setStatus(OrderStatus.PENDING);
            return orderMapper.toDTO(orderRepository.save(order));
        }
            throw new ConflictException("Order Payment was never made!");
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
