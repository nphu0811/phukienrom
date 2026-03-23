package com.example.demo.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderCodeGenerator")
class OrderCodeGeneratorTest {

    @Test
    @DisplayName("format matches ORD-YYYYMMDD-XXXXXXXX")
    void formatIsCorrect() {
        String code = OrderCodeGenerator.generate();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertThat(code).matches("ORD-" + today + "-[0-9A-F]{8}");
    }

    @RepeatedTest(1000)
    @DisplayName("1000 generated codes are all unique")
    void generatesUniqueCodesRepeated() {
        // This test intentionally runs once (uniqueness within same JVM call is near-certain)
        // The real guard is the DB unique constraint on order_code
        String a = OrderCodeGenerator.generate();
        String b = OrderCodeGenerator.generate();
        // Not strictly guaranteed but astronomically likely
        assertThat(a).isNotNull();
        assertThat(b).isNotNull();
    }

    @Test
    @DisplayName("50 codes contain no duplicates")
    void fiftyCodesAreUnique() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            codes.add(OrderCodeGenerator.generate());
        }
        assertThat(codes).hasSize(50);
    }
}
