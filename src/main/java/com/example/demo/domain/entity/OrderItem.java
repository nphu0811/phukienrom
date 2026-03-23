package com.example.demo.domain.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items", indexes = { @Index(name = "idx_order_items_order", columnList = "order_id") })
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false) private Order order;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "variant_id", nullable = false) private ProductVariant variant;
    @Column(nullable = false) private Integer quantity;
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2) private BigDecimal unitPrice;
    @Column(name = "product_name", nullable = false, length = 200) private String productName;
    @Column(name = "variant_info", length = 100) private String variantInfo;
    @Column(name = "product_image", length = 500) private String productImage;

    @Transient public BigDecimal getSubtotal() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }

    public OrderItem() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; } public void setOrder(Order order) { this.order = order; }
    public ProductVariant getVariant() { return variant; } public void setVariant(ProductVariant v) { this.variant = v; }
    public Integer getQuantity() { return quantity; } public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; } public void setUnitPrice(BigDecimal v) { this.unitPrice = v; }
    public String getProductName() { return productName; } public void setProductName(String v) { this.productName = v; }
    public String getVariantInfo() { return variantInfo; } public void setVariantInfo(String v) { this.variantInfo = v; }
    public String getProductImage() { return productImage; } public void setProductImage(String v) { this.productImage = v; }
}
