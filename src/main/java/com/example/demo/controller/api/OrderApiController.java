package com.example.demo.controller.api;
import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {
    private final OrderService orderService;
    public OrderApiController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(@AuthenticationPrincipal CustomUserDetails user,
                                                               @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(orderService.checkout(user.getUserId(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(@AuthenticationPrincipal CustomUserDetails user,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(orderService.getUserOrders(user.getUserId(), pageable)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(@AuthenticationPrincipal CustomUserDetails user,
                                                                      @PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderDetail(user.getUserId(), orderId)));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@AuthenticationPrincipal CustomUserDetails user,
                                                                   @PathVariable Long orderId,
                                                                   @RequestParam(defaultValue = "Khách hàng hủy đơn") String reason) {
        return ResponseEntity.ok(ApiResponse.success("Đơn hàng đã được hủy", orderService.cancelOrder(user.getUserId(), orderId, reason)));
    }
}
