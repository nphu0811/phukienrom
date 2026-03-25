package com.example.demo.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO mô tả một phiên bản sản phẩm.
 */
public class VariantRequest {

    @NotBlank(message = "SKU không được để trống")
    @Size(max = 100, message = "SKU tối đa 100 ký tự")
    private String sku;

    @Size(max = 50, message = "RAM tối đa 50 ký tự")
    private String ram;

    @Size(max = 50, message = "ROM tối đa 50 ký tự")
    private String rom;

    @Size(max = 50, message = "Màu tối đa 50 ký tự")
    private String color;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá phải lớn hơn hoặc bằng 0")
    @Digits(integer = 13, fraction = 2, message = "Giá không hợp lệ")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá khuyến mãi phải >= 0")
    @Digits(integer = 13, fraction = 2, message = "Giá khuyến mãi không hợp lệ")
    private BigDecimal salePrice;

    @Min(value = 0, message = "Tồn kho không được âm")
    private Integer stock = 0;

    @Size(max = 255, message = "URL ảnh tối đa 255 ký tự")
    private String imageUrl;

    private Boolean active = true;

    public VariantRequest() {}

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getRam() { return ram; }
    public void setRam(String ram) { this.ram = ram; }

    public String getRom() { return rom; }
    public void setRom(String rom) { this.rom = rom; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
