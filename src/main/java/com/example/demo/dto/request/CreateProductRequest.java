package com.example.demo.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Type-safe DTO for creating a product.
 * Replaces the previous Map<String, Object> approach which had no validation.
 */
public class CreateProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 200, message = "Tên sản phẩm tối đa 200 ký tự")
    private String name;

    @Size(max = 500, message = "Mô tả ngắn tối đa 500 ký tự")
    private String shortDescription;

    private String description;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    @Digits(integer = 13, fraction = 2, message = "Giá không hợp lệ")
    private BigDecimal basePrice;

    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;

    @NotNull(message = "Thương hiệu không được để trống")
    private Long brandId;

    private boolean featured = false;

    public CreateProductRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String v) { this.shortDescription = v; }

    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal v) { this.basePrice = v; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long v) { this.categoryId = v; }

    public Long getBrandId() { return brandId; }
    public void setBrandId(Long v) { this.brandId = v; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean v) { this.featured = v; }
}
