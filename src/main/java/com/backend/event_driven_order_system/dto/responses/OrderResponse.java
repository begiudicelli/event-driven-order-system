package com.backend.event_driven_order_system.dto.responses;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(

        Long id,
        String status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items

) {}