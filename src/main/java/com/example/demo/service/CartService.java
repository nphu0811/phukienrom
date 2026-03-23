package com.example.demo.service;
import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.response.CartResponse;

public interface CartService {
    CartResponse getCart(Long userId);
    CartResponse addItem(Long userId, CartItemRequest request);
    CartResponse updateItem(Long userId, Long itemId, Integer quantity);
    CartResponse removeItem(Long userId, Long itemId);
    void clearCart(Long userId);
    int getCartItemCount(Long userId);
}
