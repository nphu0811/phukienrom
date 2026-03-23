package com.example.demo.domain.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts", indexes = { @Index(name = "idx_carts_user", columnList = "user_id", unique = true) })
public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", unique = true) private User user;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @PreUpdate @PrePersist protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    @Transient
    public BigDecimal getTotalAmount() {
        return items.stream().map(CartItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    @Transient
    public int getTotalItems() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public Cart() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public User getUser() { return user; } public void setUser(User user) { this.user = user; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<CartItem> getItems() { return items; } public void setItems(List<CartItem> items) { this.items = items; }
}
