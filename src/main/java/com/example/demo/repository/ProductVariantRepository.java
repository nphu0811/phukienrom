package com.example.demo.repository;

import com.example.demo.domain.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProductIdAndActiveTrue(Long productId);
    Optional<ProductVariant> findBySku(String sku);

    @Modifying
    @Query("UPDATE ProductVariant v SET v.stock = v.stock - :qty WHERE v.id = :id AND v.stock >= :qty")
    int decreaseStock(@Param("id") Long id, @Param("qty") int qty);

    @Modifying
    @Query("UPDATE ProductVariant v SET v.stock = v.stock + :qty WHERE v.id = :id")
    void increaseStock(@Param("id") Long id, @Param("qty") int qty);
}
