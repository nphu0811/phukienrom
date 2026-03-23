package com.example.demo.domain.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_products_slug", columnList = "slug", unique = true),
    @Index(name = "idx_products_category", columnList = "category_id"),
    @Index(name = "idx_products_brand", columnList = "brand_id"),
    @Index(name = "idx_products_active", columnList = "active")
})
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 200) private String name;
    @Column(nullable = false, unique = true, length = 220) private String slug;
    @Column(name = "short_description", length = 500) private String shortDescription;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(name = "thumbnail_url") private String thumbnailUrl;
    @Column(nullable = false, precision = 15, scale = 2) private BigDecimal basePrice;
    @Column(name = "sale_price", precision = 15, scale = 2) private BigDecimal salePrice;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "category_id", nullable = false) private Category category;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "brand_id", nullable = false) private Brand brand;
    @Column(nullable = false) private boolean active = true;
    private boolean featured = false;
    @Column(name = "view_count") private Long viewCount = 0L;
    @Column(name = "sold_count") private Long soldCount = 0L;
    @Column(name = "avg_rating", precision = 3, scale = 2) private BigDecimal avgRating = BigDecimal.ZERO;
    @Column(name = "review_count") private Integer reviewCount = 0;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true) private List<ProductVariant> variants = new ArrayList<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true) private List<ProductImage> images = new ArrayList<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true) private List<Review> reviews = new ArrayList<>();

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public Product() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; } public void setSlug(String slug) { this.slug = slug; }
    public String getShortDescription() { return shortDescription; } public void setShortDescription(String v) { this.shortDescription = v; }
    public String getDescription() { return description; } public void setDescription(String v) { this.description = v; }
    public String getThumbnailUrl() { return thumbnailUrl; } public void setThumbnailUrl(String v) { this.thumbnailUrl = v; }
    public BigDecimal getBasePrice() { return basePrice; } public void setBasePrice(BigDecimal v) { this.basePrice = v; }
    public BigDecimal getSalePrice() { return salePrice; } public void setSalePrice(BigDecimal v) { this.salePrice = v; }
    public Category getCategory() { return category; } public void setCategory(Category v) { this.category = v; }
    public Brand getBrand() { return brand; } public void setBrand(Brand v) { this.brand = v; }
    public boolean isActive() { return active; } public void setActive(boolean v) { this.active = v; }
    public boolean isFeatured() { return featured; } public void setFeatured(boolean v) { this.featured = v; }
    public Long getViewCount() { return viewCount; } public void setViewCount(Long v) { this.viewCount = v; }
    public Long getSoldCount() { return soldCount; } public void setSoldCount(Long v) { this.soldCount = v; }
    public BigDecimal getAvgRating() { return avgRating; } public void setAvgRating(BigDecimal v) { this.avgRating = v; }
    public Integer getReviewCount() { return reviewCount; } public void setReviewCount(Integer v) { this.reviewCount = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<ProductVariant> getVariants() { return variants; } public void setVariants(List<ProductVariant> v) { this.variants = v; }
    public List<ProductImage> getImages() { return images; } public void setImages(List<ProductImage> v) { this.images = v; }
    public List<Review> getReviews() { return reviews; }
}
