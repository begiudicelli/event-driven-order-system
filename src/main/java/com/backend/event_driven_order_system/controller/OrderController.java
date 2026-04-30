package com.backend.event_driven_order_system.controller;

import com.backend.event_driven_order_system.dto.requests.CreateOrderRequest;
import com.backend.event_driven_order_system.dto.responses.OrderResponse;
import com.backend.event_driven_order_system.enums.OrderStatus;
import com.backend.event_driven_order_system.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CreateOrderRequest request
    ) {
        return ResponseEntity.ok(service.createOrder(userDetails, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll(
            @AuthenticationPrincipal UserDetails user
    ) {
        return ResponseEntity.ok(service.getMyOrders(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        return ResponseEntity.ok(service.getById(id, user));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        service.cancelOrder(id, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }
}