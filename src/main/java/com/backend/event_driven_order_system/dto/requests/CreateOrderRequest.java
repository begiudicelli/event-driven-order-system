package com.backend.event_driven_order_system.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotEmpty
        @Valid
        List<OrderItemRequest> items

) {}