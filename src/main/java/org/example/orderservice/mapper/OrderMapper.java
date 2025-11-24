package org.example.orderservice.mapper;
import org.example.orderservice.dtos.OrderDTO;
import org.example.orderservice.dtos.OrderItemDTO;
import org.example.orderservice.dtos.ReserveProductDTO;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order updateEntity(OrderDTO orderDTO, @MappingTarget Order orderItem);
    Order toEntity(OrderDTO orderDTO);

    List<ReserveProductDTO> toReserveDTO(List<OrderItem> orderItems);
    OrderDTO toDTO(Order entity);

    List<OrderItem> toEntities(List<OrderItemDTO> orderItems);

    List<OrderDTO> toDTOs(List<Order> all);
}
