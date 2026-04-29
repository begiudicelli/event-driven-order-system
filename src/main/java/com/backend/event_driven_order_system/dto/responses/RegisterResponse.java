package com.backend.event_driven_order_system.dto.responses;

public record RegisterResponse(
        Long id,
        String name,
        String email
) {}