package com.backend.event_driven_order_system.dto.responses;

import java.math.BigDecimal;

public record OrderItemResponse(

        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice

) {}