package com.example.demo.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductResponse {
    private Long id; private String name; private String slug;
    private String shortDescription; private String description; private String thumbnailUrl;
    private BigDecimal basePrice; private BigDecimal salePrice;
    private String categoryName; private String brandName;
    private BigDecimal avgRating; private Integer reviewCount; private Long soldCount;
    private boolean featured; private LocalDateTime createdAt;
    private List<VariantResponse> variants; private List<String> imageUrls;

    public ProductResponse() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; } public void setSlug(String slug) { this.slug = slug; }
    public String getShortDescription() { return shortDescription; } public void setShortDescription(String v) { this.shortDescription = v; }
    public String getDescription() { return description; } public void setDescription(String v) { this.description = v; }
    public String getThumbnailUrl() { return thumbnailUrl; } public void setThumbnailUrl(String v) { this.thumbnailUrl = v; }
    public BigDecimal getBasePrice() { return basePrice; } public void setBasePrice(BigDecimal v) { this.basePrice = v; }
    public BigDecimal getSalePrice() { return salePrice; } public void setSalePrice(BigDecimal v) { this.salePrice = v; }
    public String getCategoryName() { return categoryName; } public void setCategoryName(String v) { this.categoryName = v; }
    public String getBrandName() { return brandName; } public void setBrandName(String v) { this.brandName = v; }
    public BigDecimal getAvgRating() { return avgRating; } public void setAvgRating(BigDecimal v) { this.avgRating = v; }
    public Integer getReviewCount() { return reviewCount; } public void setReviewCount(Integer v) { this.reviewCount = v; }
    public Long getSoldCount() { return soldCount; } public void setSoldCount(Long v) { this.soldCount = v; }
    public boolean isFeatured() { return featured; } public void setFeatured(boolean featured) { this.featured = featured; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public List<VariantResponse> getVariants() { return variants; } public void setVariants(List<VariantResponse> v) { this.variants = v; }
    public List<String> getImageUrls() { return imageUrls; } public void setImageUrls(List<String> v) { this.imageUrls = v; }
}
