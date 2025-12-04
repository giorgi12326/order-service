package org.example.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.example.orderservice.dtos.CancelOrderDTO;
import org.example.orderservice.dtos.OrderDTO;
import org.example.orderservice.dtos.ReserveProductDTO;
import org.example.orderservice.dtos.ReserveResponseDTO;
import org.example.orderservice.entity.IdempotencyRecord;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderStatus;
import org.example.orderservice.exception.ConflictException;
import org.example.orderservice.exception.ResourceNotFoundException;
import org.example.orderservice.feign.InventoryClient;
import org.example.orderservice.feign.ProductClient;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.repository.IdempotentRecordRepository;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {
    public final OrderRepository orderRepository;
    public final InventoryClient inventoryClient;
    public final ProductClient productClient;
    public final OrderPersistenceService orderPersistenceService;
    public final OrderMapper orderMapper;
    public final ObjectMapper objectMapper;
    private final IdempotentRecordRepository idempotentRecordRepository;
    private final EntityManager entityManager;


    public List<OrderDTO> getAll() {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Order> all = orderRepository.findAllByUserId(principal.getId());
        return orderMapper.toDTOs(all);
    }

    public OrderDTO createOrder(OrderDTO orderDTO, String idempotencyKey) {
        List<ReserveProductDTO> reservedProducts;
        List<ReserveProductDTO> reserveDTOs = orderMapper.toReserves(orderDTO.getOrderItems());

        try {
            reservedProducts = inventoryClient.getAndReserveProducts(reserveDTOs, idempotencyKey);
        }
        catch (feign.RetryableException e){
            throw new RuntimeException("Request or Response lost rolling back, NEEDS MANUAL INTERVENTION !", e);
        }
        try{
            return fetchFataAndChangeState(reservedProducts);
        }
        catch (Exception e){
            inventoryClient.compensateReserveProducts("compensate-" + idempotencyKey);
            throw new RuntimeException("Order creation response lost!", e);
        }
    }

    private OrderDTO fetchFataAndChangeState(List<ReserveProductDTO> reservedProductsFromInventory) {
        List<ReserveResponseDTO> reservedProducts = productClient.getInfoAboutProducts(reservedProductsFromInventory);

        return orderPersistenceService.getOrderDTO(reservedProductsFromInventory, reservedProducts);
    }

    @Transactional
    public void cancelOrder(CancelOrderDTO orderDTO) {
        Order order = orderRepository.findById(orderDTO.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("ORDER NOT FOUND!"));

        if(!order.getStatus().equals(OrderStatus.PENDING))
            throw new IllegalStateException("ORDER IS ALREADY PAID FOR!");

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        List<ReserveProductDTO> list = order.getOrderItems().stream().map(dto -> ReserveProductDTO.builder().productId(dto.getProductId()).quantity(dto.getQuantity()).build()).toList();
//        inventoryClient.compensateReserveProducts(list);TODO
    }

    @Transactional
    public OrderDTO payForOrder(Long id, String idempotencyKey) {

        Optional<IdempotencyRecord> byId = idempotentRecordRepository.findById(idempotencyKey);
        if(byId.isPresent()){
            try {
                return objectMapper.readValue(byId.get().getResponseJson(), OrderDTO.class);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException("Failed to parse JSON");
            }
        }
        IdempotencyRecord record = IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .actionType("PAY_FOR_ORDER")
                .requestJson(id.toString())
                .build();

        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order Not Found!"));
        if(order.getStatus().equals(OrderStatus.PAID)){
            throw new ConflictException("Order Payment Already made!");
        }
        order.setStatus(OrderStatus.PAID);

        OrderDTO dto = orderMapper.toDTO(orderRepository.save(order));

        try {
            record.setResponseJson(objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize to JSON",e);
        }
        idempotentRecordRepository.save(record);
        return dto;
    }

    @Transactional
    public OrderDTO unpayForOrder(String idempotencyKey) {
        Optional<IdempotencyRecord> byId = idempotentRecordRepository.findById(idempotencyKey);
        if(byId.isPresent()){
            try {
                return objectMapper.readValue(byId.get().getResponseJson(), OrderDTO.class);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException("Failed to parse JSON");
            }
        }
        IdempotencyRecord record = IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .actionType("PAY_FOR_ORDER")
                .build();

        Order order = orderRepository.findById(Long.valueOf(record.getResponseJson())).orElseThrow(() -> new ResourceNotFoundException("Order Not Found!"));
        if(order.getStatus().equals(OrderStatus.PAID)){
            order.setStatus(OrderStatus.PENDING);
            OrderDTO dto = orderMapper.toDTO(orderRepository.save(order));
            try {
                record.setResponseJson(objectMapper.writeValueAsString(dto));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize to JSON",e);
            }
            idempotentRecordRepository.save(record);
            return dto;
        }
        else throw new ConflictException("Order Payment was never made!");
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
