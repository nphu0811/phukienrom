package com.example.demo.controller.web;

import com.example.demo.dto.response.ProductResponse;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.CartService;
import com.example.demo.service.ProductService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class HomeController {
    private final ProductService productService;
    private final CartService cartService;
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public HomeController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        // Run 3 DB queries in parallel using virtual threads
        CompletableFuture<List<ProductResponse>> featuredFuture =
            CompletableFuture.supplyAsync(() -> productService.getFeatured(8), executor);
        CompletableFuture<List<ProductResponse>> topSellingFuture =
            CompletableFuture.supplyAsync(() -> productService.getTopSelling(8), executor);
        CompletableFuture<List<ProductResponse>> newestFuture =
            CompletableFuture.supplyAsync(() -> productService.getNewest(8), executor);

        try {
            model.addAttribute("featuredProducts", featuredFuture.get());
            model.addAttribute("topSelling", topSellingFuture.get());
            model.addAttribute("newestProducts", newestFuture.get());
        } catch (Exception e) {
            // fallback sequential if parallel fails
            model.addAttribute("featuredProducts", productService.getFeatured(8));
            model.addAttribute("topSelling", productService.getTopSelling(8));
            model.addAttribute("newestProducts", productService.getNewest(8));
        }

        model.addAttribute("pageTitle", "Trang chủ");
        if (user != null) model.addAttribute("cartCount", cartService.getCartItemCount(user.getUserId()));
        return "index";
    }
}