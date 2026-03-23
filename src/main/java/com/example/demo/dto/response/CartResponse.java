package com.example.demo.dto.response;
import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
    private Long cartId; private List<CartItemResponse> items;
    private int totalItems; private BigDecimal totalAmount;

    public CartResponse() {}
    public Long getCartId() { return cartId; } public void setCartId(Long cartId) { this.cartId = cartId; }
    public List<CartItemResponse> getItems() { return items; } public void setItems(List<CartItemResponse> items) { this.items = items; }
    public int getTotalItems() { return totalItems; } public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
    public BigDecimal getTotalAmount() { return totalAmount; } public void setTotalAmount(BigDecimal v) { this.totalAmount = v; }
}
