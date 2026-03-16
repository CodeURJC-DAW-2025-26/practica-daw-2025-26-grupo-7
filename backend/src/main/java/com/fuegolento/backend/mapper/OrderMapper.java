package com.fuegolento.backend.mapper;

import com.fuegolento.backend.dto.OrderDTO;
import com.fuegolento.backend.dto.OrderItemDTO;
import com.fuegolento.backend.model.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderMapper(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemDTO> itemDTOs = order.getItems() == null
                ? List.of()
                : order.getItems().stream()
                .map(orderItemMapper::toDTO)
                .toList();

        return new OrderDTO(
                order.getId(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getUser() != null ? order.getUser().getId() : null,
                order.getUser() != null ? order.getUser().getUsername() : null,
                itemDTOs,
                order.getTotalPrice(),
                order.getTableNumber(),
                order.getCustomerNote()
        );
    }
}