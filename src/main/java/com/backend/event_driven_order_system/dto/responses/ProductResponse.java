package com.backend.event_driven_order_system.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
