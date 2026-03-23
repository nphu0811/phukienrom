package com.example.demo.controller.api;
import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {
    private final CartService cartService;
    public CartApiController(CartService cartService) { this.cartService = cartService; }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(user.getUserId())));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(@AuthenticationPrincipal CustomUserDetails user,
                                                             @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Đã thêm vào giỏ hàng", cartService.addItem(user.getUserId(), request)));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(@AuthenticationPrincipal CustomUserDetails user,
                                                                @PathVariable Long itemId,
                                                                @RequestParam Integer quantity) {
        return ResponseEntity.ok(ApiResponse.success(cartService.updateItem(user.getUserId(), itemId, quantity)));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(@AuthenticationPrincipal CustomUserDetails user,
                                                                @PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.success(cartService.removeItem(user.getUserId(), itemId)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal CustomUserDetails user) {
        cartService.clearCart(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Đã xóa giỏ hàng", null));
    }
}
