package com.example.demo.dto.response;
import java.math.BigDecimal;

public class OrderItemResponse {
    private Long id; private String productName; private String variantInfo;
    private String productImage; private Integer quantity;
    private BigDecimal unitPrice; private BigDecimal subtotal;

    public OrderItemResponse() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getProductName() { return productName; } public void setProductName(String v) { this.productName = v; }
    public String getVariantInfo() { return variantInfo; } public void setVariantInfo(String v) { this.variantInfo = v; }
    public String getProductImage() { return productImage; } public void setProductImage(String v) { this.productImage = v; }
    public Integer getQuantity() { return quantity; } public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; } public void setUnitPrice(BigDecimal v) { this.unitPrice = v; }
    public BigDecimal getSubtotal() { return subtotal; } public void setSubtotal(BigDecimal v) { this.subtotal = v; }
}
