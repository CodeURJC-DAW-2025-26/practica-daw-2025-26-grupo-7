package com.fuegolento.backend.mapper;

import com.fuegolento.backend.dto.OrderItemDTO;
import com.fuegolento.backend.model.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItemDTO toDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        return new OrderItemDTO(
                orderItem.getId(),
                orderItem.getDish() != null ? orderItem.getDish().getId() : null,
                orderItem.getDish() != null ? orderItem.getDish().getName() : null,
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getTotalPrice(),
                orderItem.getKitchenNote(),
                orderItem.getMeatPoint()
        );
    }
}