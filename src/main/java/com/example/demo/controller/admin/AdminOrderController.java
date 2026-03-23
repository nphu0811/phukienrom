package com.example.demo.controller.admin;
import com.example.demo.domain.enums.OrderStatus;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.service.OrderService;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
public class AdminOrderController {
    private final OrderService orderService;
    public AdminOrderController(OrderService orderService) { this.orderService = orderService; }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders(status, pageable)));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(@PathVariable Long orderId,
                                                                    @RequestParam OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Trạng thái đã được cập nhật", orderService.updateStatus(orderId, status)));
    }
}
