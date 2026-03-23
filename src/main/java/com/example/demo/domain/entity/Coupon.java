package com.example.demo.domain.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons", indexes = { @Index(name = "idx_coupons_code", columnList = "code", unique = true) })
public class Coupon {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 50) private String code;
    @Column(nullable = false, length = 200) private String description;
    @Column(name = "discount_type", nullable = false, length = 10) private String discountType;
    @Column(name = "discount_value", nullable = false, precision = 15, scale = 2) private BigDecimal discountValue;
    @Column(name = "min_order_amount", precision = 15, scale = 2) private BigDecimal minOrderAmount = BigDecimal.ZERO;
    @Column(name = "max_discount_amount", precision = 15, scale = 2) private BigDecimal maxDiscountAmount;
    @Column(name = "usage_limit") private Integer usageLimit;
    @Column(name = "used_count") private Integer usedCount = 0;
    @Column(name = "start_date") private LocalDateTime startDate;
    @Column(name = "end_date") private LocalDateTime endDate;
    @Column(nullable = false) private boolean active = true;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }

    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        if (!active) return false;
        if (startDate != null && now.isBefore(startDate)) return false;
        if (endDate != null && now.isAfter(endDate)) return false;
        if (usageLimit != null && usedCount >= usageLimit) return false;
        return true;
    }

    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if ("PERCENT".equals(discountType)) {
            BigDecimal discount = orderAmount.multiply(discountValue).divide(BigDecimal.valueOf(100));
            if (maxDiscountAmount != null) discount = discount.min(maxDiscountAmount);
            return discount;
        } else {
            return discountValue.min(orderAmount);
        }
    }

    public Coupon() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getCode() { return code; } public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; } public void setDescription(String v) { this.description = v; }
    public String getDiscountType() { return discountType; } public void setDiscountType(String v) { this.discountType = v; }
    public BigDecimal getDiscountValue() { return discountValue; } public void setDiscountValue(BigDecimal v) { this.discountValue = v; }
    public BigDecimal getMinOrderAmount() { return minOrderAmount; } public void setMinOrderAmount(BigDecimal v) { this.minOrderAmount = v; }
    public BigDecimal getMaxDiscountAmount() { return maxDiscountAmount; } public void setMaxDiscountAmount(BigDecimal v) { this.maxDiscountAmount = v; }
    public Integer getUsageLimit() { return usageLimit; } public void setUsageLimit(Integer v) { this.usageLimit = v; }
    public Integer getUsedCount() { return usedCount; } public void setUsedCount(Integer v) { this.usedCount = v; }
    public LocalDateTime getStartDate() { return startDate; } public void setStartDate(LocalDateTime v) { this.startDate = v; }
    public LocalDateTime getEndDate() { return endDate; } public void setEndDate(LocalDateTime v) { this.endDate = v; }
    public boolean isActive() { return active; } public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
