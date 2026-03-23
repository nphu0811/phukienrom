package com.example.demo.domain.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist_items", indexes = {
    @Index(name = "idx_wishlist_user", columnList = "user_id"),
    @Index(name = "idx_wishlist_user_product", columnList = "user_id, product_id", unique = true)
})
public class WishlistItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "product_id", nullable = false) private Product product;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }

    public WishlistItem() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public User getUser() { return user; } public void setUser(User user) { this.user = user; }
    public Product getProduct() { return product; } public void setProduct(Product p) { this.product = p; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
