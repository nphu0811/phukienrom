package com.example.demo.domain.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_reviews_product", columnList = "product_id"),
    @Index(name = "idx_reviews_user", columnList = "user_id"),
    @Index(name = "idx_reviews_user_product", columnList = "user_id, product_id", unique = true)
})
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id", nullable = false) private Product product;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(nullable = false) private Integer rating;
    @Column(columnDefinition = "TEXT") private String comment;
    @Column(name = "reply", columnDefinition = "TEXT") private String reply;
    @Column(nullable = false) private boolean approved = true;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }

    public Review() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; } public void setProduct(Product p) { this.product = p; }
    public User getUser() { return user; } public void setUser(User user) { this.user = user; }
    public Integer getRating() { return rating; } public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; } public void setComment(String comment) { this.comment = comment; }
    public String getReply() { return reply; } public void setReply(String reply) { this.reply = reply; }
    public boolean isApproved() { return approved; } public void setApproved(boolean approved) { this.approved = approved; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
