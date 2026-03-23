package com.example.demo.domain.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items", indexes = {
    @Index(name = "idx_cart_items_cart", columnList = "cart_id"),
    @Index(name = "idx_cart_items_variant", columnList = "variant_id")
})
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "cart_id", nullable = false) private Cart cart;
    // LAZY: CartRepository.findByUserIdWithItems uses JOIN FETCH to load variants explicitly
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "variant_id", nullable = false) private ProductVariant variant;
    @Column(nullable = false) private Integer quantity;
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2) private BigDecimal unitPrice;

    @Transient
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public CartItem() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Cart getCart() { return cart; } public void setCart(Cart cart) { this.cart = cart; }
    public ProductVariant getVariant() { return variant; } public void setVariant(ProductVariant v) { this.variant = v; }
    public Integer getQuantity() { return quantity; } public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; } public void setUnitPrice(BigDecimal v) { this.unitPrice = v; }
}
