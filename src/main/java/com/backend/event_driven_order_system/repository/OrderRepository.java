package com.backend.event_driven_order_system.repository;

import com.backend.event_driven_order_system.entity.Order;
import com.backend.event_driven_order_system.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserEmail(String email);
    Optional<Order> findByIdAndUserEmail(Long id, String email);
    List<Order> findByUserEmailAndStatus(String email, OrderStatus status);
}