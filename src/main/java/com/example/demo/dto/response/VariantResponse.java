package com.example.demo.dto.response;
import java.math.BigDecimal;

public class VariantResponse {
    private Long id; private String sku; private String ram; private String rom;
    private String color; private BigDecimal price; private BigDecimal salePrice;
    private BigDecimal effectivePrice; private Integer stock; private String imageUrl; private boolean active;

    public VariantResponse() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getSku() { return sku; } public void setSku(String sku) { this.sku = sku; }
    public String getRam() { return ram; } public void setRam(String ram) { this.ram = ram; }
    public String getRom() { return rom; } public void setRom(String rom) { this.rom = rom; }
    public String getColor() { return color; } public void setColor(String color) { this.color = color; }
    public BigDecimal getPrice() { return price; } public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getSalePrice() { return salePrice; } public void setSalePrice(BigDecimal v) { this.salePrice = v; }
    public BigDecimal getEffectivePrice() { return effectivePrice; } public void setEffectivePrice(BigDecimal v) { this.effectivePrice = v; }
    public Integer getStock() { return stock; } public void setStock(Integer stock) { this.stock = stock; }
    public String getImageUrl() { return imageUrl; } public void setImageUrl(String v) { this.imageUrl = v; }
    public boolean isActive() { return active; } public void setActive(boolean active) { this.active = active; }
}
