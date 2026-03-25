package com.example.demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Type-safe DTO for updating a product (all fields optional / patch-style).
 * Replaces the previous Map<String, Object> approach.
 */
public class UpdateProductRequest {

    @Size(max = 200, message = "Tên sản phẩm tối đa 200 ký tự")
    private String name;

    @Size(max = 500, message = "Mô tả ngắn tối đa 500 ký tự")
    private String shortDescription;

    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    @Digits(integer = 13, fraction = 2, message = "Giá không hợp lệ")
    private BigDecimal basePrice;

    private Boolean featured;

    @Valid
    private List<VariantRequest> variants;

    public UpdateProductRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String v) { this.shortDescription = v; }

    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal v) { this.basePrice = v; }

    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean v) { this.featured = v; }

    public List<VariantRequest> getVariants() { return variants; }
    public void setVariants(List<VariantRequest> variants) { this.variants = variants; }
}
