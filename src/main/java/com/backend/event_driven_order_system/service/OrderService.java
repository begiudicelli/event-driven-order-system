package com.backend.event_driven_order_system.service;

import com.backend.event_driven_order_system.dto.requests.CreateOrderRequest;
import com.backend.event_driven_order_system.dto.requests.OrderItemRequest;
import com.backend.event_driven_order_system.dto.responses.OrderItemResponse;
import com.backend.event_driven_order_system.dto.responses.OrderResponse;
import com.backend.event_driven_order_system.entity.*;
import com.backend.event_driven_order_system.enums.OrderStatus;
import com.backend.event_driven_order_system.exception.BusinessException;
import com.backend.event_driven_order_system.exception.ResourceNotFoundException;
import com.backend.event_driven_order_system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse createOrder(UserDetails userDetails, CreateOrderRequest request) {

        if (request.items().isEmpty()) {
            throw new BusinessException("Order must have at least one item");
        }

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.items()) {

            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (product.getStock() < itemReq.quantity()) {
                throw new BusinessException(
                        "Insufficient stock for product: " + product.getName()
                );
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemReq.quantity());
            item.setUnitPrice(product.getPrice());

            product.setStock(product.getStock() - itemReq.quantity());

            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemReq.quantity()));

            total = total.add(itemTotal);

            items.add(item);
        }

        order.setItems(items);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);

        return toResponse(saved);
    }


    public List<OrderResponse> getMyOrders(UserDetails userDetails) {
        return orderRepository.findByUserEmail(userDetails.getUsername())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse getById(Long id, UserDetails userDetails) {
        Order order = orderRepository.findByIdAndUserEmail(id, userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return toResponse(order);
    }

    @Transactional
    public void cancelOrder(Long id, UserDetails userDetails) {
        Order order = orderRepository.findByIdAndUserEmail(id, userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Only PENDING orders can be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(status);

        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getUnitPrice()
                        ))
                        .toList()
        );
    }
}