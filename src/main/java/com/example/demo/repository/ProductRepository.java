package com.example.demo.repository;

import com.example.demo.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySlugAndActiveTrue(String slug);

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByFeaturedTrueAndActiveTrue(Pageable pageable);

    /**
     * Advanced filter query with optional parameters.
     * WHY JPQL: Avoids N+1, keeps type-safety, works with pagination.
     */
    @Query(value = """
            SELECT p.* FROM products p
            JOIN categories c ON c.id = p.category_id
            JOIN brands b ON b.id = p.brand_id
            WHERE p.active = true
            AND (CAST(:categorySlug AS text) IS NULL OR c.slug = :categorySlug)
            AND (CAST(:brandSlug AS text) IS NULL OR b.slug = :brandSlug)
            AND (CAST(:minPrice AS numeric) IS NULL OR p.base_price >= CAST(:minPrice AS numeric))
            AND (CAST(:maxPrice AS numeric) IS NULL OR p.base_price <= CAST(:maxPrice AS numeric))
            AND (CAST(:keyword AS text) IS NULL OR LOWER(p.name::text) LIKE CAST(:keyword AS text))
            """,
            countQuery = """
            SELECT COUNT(*) FROM products p
            JOIN categories c ON c.id = p.category_id
            JOIN brands b ON b.id = p.brand_id
            WHERE p.active = true
            AND (CAST(:categorySlug AS text) IS NULL OR c.slug = :categorySlug)
            AND (CAST(:brandSlug AS text) IS NULL OR b.slug = :brandSlug)
            AND (CAST(:minPrice AS numeric) IS NULL OR p.base_price >= CAST(:minPrice AS numeric))
            AND (CAST(:maxPrice AS numeric) IS NULL OR p.base_price <= CAST(:maxPrice AS numeric))
            AND (CAST(:keyword AS text) IS NULL OR LOWER(p.name::text) LIKE CAST(:keyword AS text))
            """,
            nativeQuery = true)
    
    Page<Product> findWithFilters(
            @Param("categorySlug") String categorySlug,
            @Param("brandSlug") String brandSlug,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.soldCount DESC")
    List<Product> findTopSelling(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.createdAt DESC")
    List<Product> findNewest(Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Product p SET p.soldCount = p.soldCount + :qty WHERE p.id = :id")
    void incrementSoldCount(@Param("id") Long id, @Param("qty") int qty);
}