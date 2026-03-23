package com.example.demo.controller.web;
import com.example.demo.repository.BrandRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.CartService;
import com.example.demo.service.ProductService;
import com.example.demo.service.impl.ReviewService;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@Controller
public class ProductWebController {
    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ReviewService reviewService;
    private final CartService cartService;

    public ProductWebController(ProductService productService, CategoryRepository categoryRepository,
                                 BrandRepository brandRepository, ReviewService reviewService, CartService cartService) {
        this.productService = productService; this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository; this.reviewService = reviewService; this.cartService = cartService;
    }

    @GetMapping("/products")
    public String listProducts(@RequestParam(required = false) String category,
                                @RequestParam(required = false) String brand,
                                @RequestParam(required = false) String priceRange,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(defaultValue = "createdAt,desc") String sort,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "12") int size,
                                Model model, @AuthenticationPrincipal CustomUserDetails user) {
        BigDecimal minPrice = null, maxPrice = null;
        if (priceRange != null && priceRange.contains("-")) {
            String[] parts = priceRange.split("-");
            minPrice = new BigDecimal(parts[0]); maxPrice = new BigDecimal(parts[1]);
        }
        String[] sortParts = sort.split(",");
        String sortBy = sortParts[0];
        String sortDir = sortParts.length > 1 ? sortParts[1] : "desc";
        Sort pageSort = sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, pageSort);

        model.addAttribute("products", productService.getProducts(category, brand, minPrice, maxPrice, keyword, pageable));
        model.addAttribute("categories", categoryRepository.findByParentIsNullAndActiveTrue());
        model.addAttribute("brands", brandRepository.findByActiveTrue());
        model.addAttribute("category", category); model.addAttribute("brand", brand);
        model.addAttribute("priceRange", priceRange); model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort); model.addAttribute("pageTitle", "Tất cả sản phẩm");
        if (user != null) model.addAttribute("cartCount", cartService.getCartItemCount(user.getUserId()));
        return "product/list";
    }

    @GetMapping("/products/{slug}")
    public String productDetail(@PathVariable String slug, @RequestParam(defaultValue = "0") int reviewPage,
                                 Model model, @AuthenticationPrincipal CustomUserDetails user) {
        var product = productService.getBySlug(slug);
        var reviews = reviewService.getProductReviews(product.getId(),
            PageRequest.of(reviewPage, 5, Sort.by("createdAt").descending()));
        model.addAttribute("product", product); model.addAttribute("reviews", reviews);
        model.addAttribute("pageTitle", product.getName());
        model.addAttribute("relatedProducts",
            productService.getProducts(null, product.getBrandName(), null, null, null, PageRequest.of(0, 6)).getContent());
        if (user != null) {
            model.addAttribute("cartCount", cartService.getCartItemCount(user.getUserId()));
            model.addAttribute("userId", user.getUserId());
        }
        return "product/detail";
    }
}
