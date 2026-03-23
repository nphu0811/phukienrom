package com.example.demo.domain.entity;

import com.example.demo.domain.enums.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Order entity — business logic")
class OrderTest {

    @Test
    @DisplayName("PENDING order → cancellable")
    void pendingOrderIsCancellable() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        assertThat(order.isCancellable()).isTrue();
    }

    @Test
    @DisplayName("CONFIRMED order → cancellable")
    void confirmedOrderIsCancellable() {
        Order order = new Order();
        order.setStatus(OrderStatus.CONFIRMED);
        assertThat(order.isCancellable()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"SHIPPING", "COMPLETED", "CANCELLED"})
    @DisplayName("SHIPPING / COMPLETED / CANCELLED → not cancellable")
    void nonCancellableStatuses(OrderStatus status) {
        Order order = new Order();
        order.setStatus(status);
        assertThat(order.isCancellable()).isFalse();
    }
}
