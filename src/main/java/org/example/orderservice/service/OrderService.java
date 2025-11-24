package org.example.orderservice.service;

import lombok.AllArgsConstructor;
import org.example.orderservice.dtos.OrderDTO;
import org.example.orderservice.dtos.ReserveProductDTO;
import org.example.orderservice.dtos.ReserveResponseDTO;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderItem;
import org.example.orderservice.entity.OrderStatus;
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
    public List<OrderDTO> getAll() {
        System.out.println("Getting all orders From Database!");
        System.out.println(cacheManager.getCache("order-cache"));

        return orderMapper.toDTOs(orderRepository.findAll());
    }

    @CacheEvict(value = "order-cache", allEntries = true)
    public OrderDTO createOrder(OrderDTO orderDTO) {
        if (!userClient.userExists(orderDTO.getUserId())) {
            throw new RuntimeException("User does not exist");
        }

        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> items = orderMapper.toEntities(orderDTO.getOrderItems());
        for (OrderItem item : items) {
            item.setOrder(order);
        }

        order.setOrderItems(items);

        List<ReserveProductDTO> reserveDTOs = orderMapper.toReserveDTO(items);
        List<ReserveResponseDTO> reservedProducts = productClient.getAndReserveProducts(reserveDTOs);

        float amount = reservedProducts.stream()
                .map(r -> r.getPrice() * r.getQuantity())
                .reduce(0f, Float::sum);
        order.setAmount(amount);

        return orderMapper.toDTO(orderRepository.save(order));
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
