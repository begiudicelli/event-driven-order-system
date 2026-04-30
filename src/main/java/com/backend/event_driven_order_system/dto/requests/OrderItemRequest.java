package com.backend.event_driven_order_system.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(

        @NotNull
        Long productId,

        @NotNull
        @Min(1)
        Integer quantity

) {}