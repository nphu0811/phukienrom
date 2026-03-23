package com.example.demo.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Coupon entity — business logic")
class CouponTest {

    private Coupon coupon;

    @BeforeEach
    void setUp() {
        coupon = new Coupon();
        coupon.setCode("TEST10");
        coupon.setDescription("Test coupon");
        coupon.setActive(true);
        coupon.setUsedCount(0);
        coupon.setMinOrderAmount(BigDecimal.ZERO);
    }

    // ── isValid() ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("isValid()")
    class IsValidTests {

        @Test
        @DisplayName("active, no dates, no limit → valid")
        void validWhenActiveNoConstraints() {
            assertThat(coupon.isValid()).isTrue();
        }

        @Test
        @DisplayName("inactive → invalid")
        void invalidWhenInactive() {
            coupon.setActive(false);
            assertThat(coupon.isValid()).isFalse();
        }

        @Test
        @DisplayName("before startDate → invalid")
        void invalidBeforeStartDate() {
            coupon.setStartDate(LocalDateTime.now().plusDays(1));
            assertThat(coupon.isValid()).isFalse();
        }

        @Test
        @DisplayName("after endDate → invalid")
        void invalidAfterEndDate() {
            coupon.setEndDate(LocalDateTime.now().minusDays(1));
            assertThat(coupon.isValid()).isFalse();
        }

        @Test
        @DisplayName("within valid date range → valid")
        void validWithinDateRange() {
            coupon.setStartDate(LocalDateTime.now().minusDays(1));
            coupon.setEndDate(LocalDateTime.now().plusDays(1));
            assertThat(coupon.isValid()).isTrue();
        }

        @Test
        @DisplayName("usage limit reached → invalid")
        void invalidWhenUsageLimitReached() {
            coupon.setUsageLimit(5);
            coupon.setUsedCount(5);
            assertThat(coupon.isValid()).isFalse();
        }

        @Test
        @DisplayName("usage limit not yet reached → valid")
        void validWhenUsageBelowLimit() {
            coupon.setUsageLimit(5);
            coupon.setUsedCount(4);
            assertThat(coupon.isValid()).isTrue();
        }

        @Test
        @DisplayName("null usage limit (unlimited) → valid")
        void validWhenNullUsageLimit() {
            coupon.setUsageLimit(null);
            coupon.setUsedCount(999);
            assertThat(coupon.isValid()).isTrue();
        }
    }

    // ── calculateDiscount() ───────────────────────────────────────────────

    @Nested
    @DisplayName("calculateDiscount()")
    class CalculateDiscountTests {

        @Test
        @DisplayName("PERCENT — 10% of 500,000 = 50,000")
        void percentDiscountBasic() {
            coupon.setDiscountType("PERCENT");
            coupon.setDiscountValue(new BigDecimal("10"));

            BigDecimal discount = coupon.calculateDiscount(new BigDecimal("500000"));
            assertThat(discount).isEqualByComparingTo("50000.00");
        }

        @Test
        @DisplayName("PERCENT — capped at maxDiscountAmount")
        void percentDiscountCappedAtMax() {
            coupon.setDiscountType("PERCENT");
            coupon.setDiscountValue(new BigDecimal("50"));
            coupon.setMaxDiscountAmount(new BigDecimal("100000"));

            // 50% of 500,000 = 250,000 but capped at 100,000
            BigDecimal discount = coupon.calculateDiscount(new BigDecimal("500000"));
            assertThat(discount).isEqualByComparingTo("100000");
        }

        @Test
        @DisplayName("PERCENT — no max cap, full percentage applies")
        void percentDiscountNoMaxCap() {
            coupon.setDiscountType("PERCENT");
            coupon.setDiscountValue(new BigDecimal("20"));
            coupon.setMaxDiscountAmount(null);

            BigDecimal discount = coupon.calculateDiscount(new BigDecimal("300000"));
            assertThat(discount).isEqualByComparingTo("60000.00");
        }

        @Test
        @DisplayName("FIXED — discount value less than order amount")
        void fixedDiscountNormal() {
            coupon.setDiscountType("FIXED");
            coupon.setDiscountValue(new BigDecimal("50000"));

            BigDecimal discount = coupon.calculateDiscount(new BigDecimal("200000"));
            assertThat(discount).isEqualByComparingTo("50000");
        }

        @Test
        @DisplayName("FIXED — discount cannot exceed order amount")
        void fixedDiscountCannotExceedOrder() {
            coupon.setDiscountType("FIXED");
            coupon.setDiscountValue(new BigDecimal("300000"));

            // Discount capped at order amount
            BigDecimal discount = coupon.calculateDiscount(new BigDecimal("100000"));
            assertThat(discount).isEqualByComparingTo("100000");
        }
    }
}
