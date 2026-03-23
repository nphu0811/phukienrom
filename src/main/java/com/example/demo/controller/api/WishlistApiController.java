package com.example.demo.controller.api;

import com.example.demo.domain.entity.Product;
import com.example.demo.domain.entity.WishlistItem;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WishlistItemRepository;
import com.example.demo.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistApiController {

    private final WishlistItemRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public WishlistApiController(WishlistItemRepository wishlistRepository,
                                  ProductRepository productRepository,
                                  UserRepository userRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /** Kiểm tra sản phẩm có trong wishlist không */
    @GetMapping("/{productId}/status")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> getStatus(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails user) {
        if (user == null) {
            return ResponseEntity.ok(ApiResponse.success(Map.of("inWishlist", false)));
        }
        boolean inWishlist = wishlistRepository.existsByUserIdAndProductId(user.getUserId(), productId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("inWishlist", inWishlist)));
    }

    /** Toggle: thêm nếu chưa có, xoá nếu đã có */
    @PostMapping("/{productId}/toggle")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> toggle(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails user) {

        Long userId = user.getUserId();
        boolean exists = wishlistRepository.existsByUserIdAndProductId(userId, productId);

        if (exists) {
            wishlistRepository.deleteByUserIdAndProductId(userId, productId);
            return ResponseEntity.ok(ApiResponse.success("Đã xoá khỏi yêu thích", Map.of("inWishlist", false)));
        } else {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
            WishlistItem item = new WishlistItem();
            item.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId)));
            item.setProduct(product);
            wishlistRepository.save(item);
            return ResponseEntity.ok(ApiResponse.success("Đã thêm vào yêu thích", Map.of("inWishlist", true)));
        }
    }
}
