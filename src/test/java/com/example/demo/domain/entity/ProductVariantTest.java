package com.example.demo.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductVariant — getEffectivePrice()")
class ProductVariantTest {

    @Test
    @DisplayName("salePrice set and > 0 → returns salePrice")
    void returnsSalePriceWhenSet() {
        ProductVariant v = new ProductVariant();
        v.setPrice(new BigDecimal("2000000"));
        v.setSalePrice(new BigDecimal("1800000"));

        assertThat(v.getEffectivePrice()).isEqualByComparingTo("1800000");
    }

    @Test
    @DisplayName("salePrice is zero → falls back to price")
    void fallsBackToPriceWhenSalePriceIsZero() {
        ProductVariant v = new ProductVariant();
        v.setPrice(new BigDecimal("2000000"));
        v.setSalePrice(BigDecimal.ZERO);

        assertThat(v.getEffectivePrice()).isEqualByComparingTo("2000000");
    }

    @Test
    @DisplayName("salePrice is null → returns variant price")
    void fallsBackToPriceWhenSalePriceIsNull() {
        ProductVariant v = new ProductVariant();
        v.setPrice(new BigDecimal("1500000"));
        v.setSalePrice(null);

        assertThat(v.getEffectivePrice()).isEqualByComparingTo("1500000");
    }

    @Test
    @DisplayName("price is null but product.basePrice set → returns basePrice")
    void fallsBackToProductBasePrice() {
        Product product = new Product();
        product.setBasePrice(new BigDecimal("999000"));

        ProductVariant v = new ProductVariant();
        v.setProduct(product);
        v.setPrice(null);
        v.setSalePrice(null);

        assertThat(v.getEffectivePrice()).isEqualByComparingTo("999000");
    }

    @Test
    @DisplayName("price null, product null → returns zero")
    void returnsZeroWhenAllNull() {
        ProductVariant v = new ProductVariant();
        v.setPrice(null);
        v.setSalePrice(null);

        assertThat(v.getEffectivePrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
