package org.example.orderservice.mapper;
import org.example.orderservice.dtos.OrderDTO;
import org.example.orderservice.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order updateEntity(OrderDTO orderDTO, @MappingTarget Order order);
    Order toEntity(OrderDTO orderDTO);
    OrderDTO toDTO(Order entity);
}
