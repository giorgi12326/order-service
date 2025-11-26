package org.example.orderservice.service;

import lombok.AllArgsConstructor;
import org.example.orderservice.dtos.OrderDTO;
import org.example.orderservice.dtos.ReserveProductDTO;
import org.example.orderservice.dtos.ReserveResponseDTO;
import org.example.orderservice.entity.Order;
import org.example.orderservice.feign.ProductClient;
import org.example.orderservice.feign.UserClient;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.security.CustomUserDetails;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
public class OrderService {
    public final OrderRepository orderRepository;
    public final ProductClient productClient;
    public final OrderPersistenceService orderPersistenceService;
    public final OrderMapper orderMapper;

    @Cacheable(value = "order-cache")
    public List<OrderDTO> getAll() {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Order> all = orderRepository.findAllByUserId(principal.getId());
        return orderMapper.toDTOs(all);
    }

    public OrderDTO createOrder(OrderDTO orderDTO) {
        List<ReserveProductDTO> reserveDTOs = orderMapper.toReserves(orderDTO.getOrderItems());
        List<ReserveResponseDTO> reservedProducts;
        try {//here default to compensating , because i assumed that B commits more often then not
            reservedProducts = productClient.getAndReserveProducts(reserveDTOs);
        }
        catch (feign.RetryableException e){
            productClient.compensateReserveProducts(reserveDTOs);
            throw new RuntimeException("Order creation response lost!", e);
        }
        try{
            return orderPersistenceService.getOrderDTO(reservedProducts);
        }
        catch (Exception e){
            productClient.compensateReserveProducts(reserveDTOs);
            throw new RuntimeException("Order creation response lost!", e);
        }
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
