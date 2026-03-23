package com.example.demo.domain.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id", nullable = false) private Product product;
    @Column(name = "image_url", nullable = false) private String imageUrl;
    @Column(name = "public_id") private String publicId;
    @Column(name = "display_order") private Integer displayOrder = 0;
    @Column(name = "is_primary") private boolean primary = false;

    public ProductImage() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; } public void setProduct(Product p) { this.product = p; }
    public String getImageUrl() { return imageUrl; } public void setImageUrl(String v) { this.imageUrl = v; }
    public String getPublicId() { return publicId; } public void setPublicId(String v) { this.publicId = v; }
    public Integer getDisplayOrder() { return displayOrder; } public void setDisplayOrder(Integer v) { this.displayOrder = v; }
    public boolean isPrimary() { return primary; } public void setPrimary(boolean v) { this.primary = v; }
}
