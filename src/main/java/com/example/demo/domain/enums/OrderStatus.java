package com.example.demo.domain.enums;

public enum OrderStatus {
    PENDING,        // Chờ xác nhận
    CONFIRMED,      // Đã xác nhận
    PROCESSING,     // Đang xử lý
    SHIPPING,       // Đang giao hàng
    DELIVERED,      // Đã giao
    COMPLETED,      // Hoàn thành
    CANCELLED,      // Đã hủy
    REFUNDED        // Đã hoàn tiền
}
