package com.example.demo.service;
import com.example.demo.domain.enums.OrderStatus;
import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse checkout(Long userId, CheckoutRequest request);
    Page<OrderResponse> getUserOrders(Long userId, Pageable pageable);
    OrderResponse getOrderDetail(Long userId, Long orderId);
    OrderResponse cancelOrder(Long userId, Long orderId, String reason);
    Page<OrderResponse> getAllOrders(OrderStatus status, Pageable pageable);
    OrderResponse updateStatus(Long orderId, OrderStatus newStatus);
}
