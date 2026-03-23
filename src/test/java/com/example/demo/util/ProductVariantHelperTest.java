package com.example.demo.util;

import com.example.demo.domain.entity.ProductVariant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductVariantHelper.buildVariantInfo()")
class ProductVariantHelperTest {

    private ProductVariant variant(String ram, String rom, String color) {
        ProductVariant v = new ProductVariant();
        v.setRam(ram);
        v.setRom(rom);
        v.setColor(color);
        return v;
    }

    @Test
    @DisplayName("all three fields → joined with ' / '")
    void allFields() {
        assertThat(ProductVariantHelper.buildVariantInfo(variant("8GB", "128GB", "Black")))
                .isEqualTo("8GB / 128GB / Black");
    }

    @Test
    @DisplayName("only RAM → returns RAM only")
    void ramOnly() {
        assertThat(ProductVariantHelper.buildVariantInfo(variant("8GB", null, null)))
                .isEqualTo("8GB");
    }

    @Test
    @DisplayName("only ROM → returns ROM only")
    void romOnly() {
        assertThat(ProductVariantHelper.buildVariantInfo(variant(null, "256GB", null)))
                .isEqualTo("256GB");
    }

    @Test
    @DisplayName("only Color → returns Color only")
    void colorOnly() {
        assertThat(ProductVariantHelper.buildVariantInfo(variant(null, null, "White")))
                .isEqualTo("White");
    }

    @Test
    @DisplayName("RAM + Color (no ROM) → 'RAM / Color'")
    void ramAndColor() {
        assertThat(ProductVariantHelper.buildVariantInfo(variant("12GB", null, "Blue")))
                .isEqualTo("12GB / Blue");
    }

    @Test
    @DisplayName("all null → empty string")
    void allNull() {
        assertThat(ProductVariantHelper.buildVariantInfo(variant(null, null, null)))
                .isEmpty();
    }
}
