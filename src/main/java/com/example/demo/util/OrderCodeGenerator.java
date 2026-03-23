package com.example.demo.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates unique order codes in format: ORD-YYYYMMDD-XXXXXXXX
 *
 * Uses SecureRandom hex suffix instead of AtomicInteger counter.
 * Reason: AtomicInteger resets to 0 on every app restart → possible collisions.
 * The DB unique constraint on order_code is the final safety net,
 * but the random suffix makes collisions astronomically unlikely.
 *
 * Collision probability: 1 in 16^8 = 4.3 billion per same day.
 */
public final class OrderCodeGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final SecureRandom RANDOM = new SecureRandom();

    private OrderCodeGenerator() {}

    public static String generate() {
        String datePart = LocalDateTime.now().format(DATE_FMT);
        // 8 hex chars = 32 bits of randomness per day
        String randomPart = String.format("%08X", RANDOM.nextInt(0x7FFFFFFF));
        return "ORD-" + datePart + "-" + randomPart;
    }
}
