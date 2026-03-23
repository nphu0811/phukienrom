package com.example.demo.util;

import com.example.demo.domain.entity.ProductVariant;

/**
 * Utility class for ProductVariant display helpers.
 * Eliminates duplicate buildVariantInfo logic across CartServiceImpl and OrderServiceImpl.
 */
public final class ProductVariantHelper {

    private ProductVariantHelper() {}

    /**
     * Build a human-readable variant description string.
     * Example: "8GB / 128GB / Black"
     */
    public static String buildVariantInfo(ProductVariant v) {
        StringBuilder sb = new StringBuilder();
        if (v.getRam() != null) sb.append(v.getRam());
        if (v.getRom() != null) {
            if (!sb.isEmpty()) sb.append(" / ");
            sb.append(v.getRom());
        }
        if (v.getColor() != null) {
            if (!sb.isEmpty()) sb.append(" / ");
            sb.append(v.getColor());
        }
        return sb.toString();
    }
}
