package com.example.demo.dto.request;
import jakarta.validation.constraints.*;

public class CartItemRequest {
    @NotNull(message = "Variant không được để trống") private Long variantId;
    @NotNull @Min(value = 1, message = "Số lượng phải >= 1") private Integer quantity;

    public CartItemRequest() {}
    public Long getVariantId() { return variantId; } public void setVariantId(Long variantId) { this.variantId = variantId; }
    public Integer getQuantity() { return quantity; } public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
