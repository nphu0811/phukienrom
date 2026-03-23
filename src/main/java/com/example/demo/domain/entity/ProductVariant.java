package com.example.demo.domain.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variants", indexes = {
    @Index(name = "idx_variants_product", columnList = "product_id"),
    @Index(name = "idx_variants_sku", columnList = "sku", unique = true)
})
public class ProductVariant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id", nullable = false) private Product product;
    @Column(nullable = false, unique = true, length = 100) private String sku;
    @Column(length = 50) private String ram;
    @Column(length = 50) private String rom;
    @Column(length = 50) private String color;
    @Column(precision = 15, scale = 2) private BigDecimal price;
    @Column(name = "sale_price", precision = 15, scale = 2) private BigDecimal salePrice;
    @Column(nullable = false) private Integer stock = 0;
    @Column(name = "image_url") private String imageUrl;
    @Column(nullable = false) private boolean active = true;

    @Transient
    public BigDecimal getEffectivePrice() {
        if (salePrice != null && salePrice.compareTo(BigDecimal.ZERO) > 0) return salePrice;
        return price != null ? price : (product != null ? product.getBasePrice() : BigDecimal.ZERO);
    }

    public ProductVariant() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; } public void setProduct(Product p) { this.product = p; }
    public String getSku() { return sku; } public void setSku(String sku) { this.sku = sku; }
    public String getRam() { return ram; } public void setRam(String ram) { this.ram = ram; }
    public String getRom() { return rom; } public void setRom(String rom) { this.rom = rom; }
    public String getColor() { return color; } public void setColor(String color) { this.color = color; }
    public BigDecimal getPrice() { return price; } public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getSalePrice() { return salePrice; } public void setSalePrice(BigDecimal v) { this.salePrice = v; }
    public Integer getStock() { return stock; } public void setStock(Integer stock) { this.stock = stock; }
    public String getImageUrl() { return imageUrl; } public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public boolean isActive() { return active; } public void setActive(boolean active) { this.active = active; }
}
