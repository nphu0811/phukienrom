package com.example.demo.service;

import com.example.demo.dto.request.CreateProductRequest;
import com.example.demo.dto.request.UpdateProductRequest;
import com.example.demo.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    Page<ProductResponse> getProducts(String category, String brand, BigDecimal minPrice,
                                      BigDecimal maxPrice, String keyword, Pageable pageable);
    ProductResponse getBySlug(String slug);
    List<ProductResponse> getTopSelling(int limit);
    List<ProductResponse> getNewest(int limit);
    List<ProductResponse> getFeatured(int limit);

    /** Create product with typed DTO — replaces Map<String, Object> */
    ProductResponse create(CreateProductRequest request, List<MultipartFile> images);

    /** Update product with typed DTO — replaces Map<String, Object> */
    ProductResponse update(Long id, UpdateProductRequest request);

    ProductResponse addImages(Long id, List<MultipartFile> images);

    void deleteImage(Long productId, Long imageId);

    void delete(Long id);
    void toggleActive(Long id);
}
