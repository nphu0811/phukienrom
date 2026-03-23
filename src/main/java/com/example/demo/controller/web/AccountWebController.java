package com.example.demo.controller.web;
import com.example.demo.repository.*;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/account")
public class AccountWebController {
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final WishlistItemRepository wishlistRepository;
    private final CartService cartService;

    public AccountWebController(OrderService orderService, UserRepository userRepository,
                                 AddressRepository addressRepository, WishlistItemRepository wishlistRepository,
                                 CartService cartService) {
        this.orderService = orderService; this.userRepository = userRepository;
        this.addressRepository = addressRepository; this.wishlistRepository = wishlistRepository;
        this.cartService = cartService;
    }

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        model.addAttribute("currentUser", userRepository.findById(user.getUserId()).orElseThrow());
        model.addAttribute("cartCount", cartService.getCartItemCount(user.getUserId()));
        model.addAttribute("pageTitle", "Tài khoản của tôi");
        return "user/profile";
    }

    @GetMapping("/orders")
    public String orders(@RequestParam(defaultValue = "0") int page, Model model,
                          @AuthenticationPrincipal CustomUserDetails user) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        model.addAttribute("orders", orderService.getUserOrders(user.getUserId(), pageable));
        model.addAttribute("cartCount", cartService.getCartItemCount(user.getUserId()));
        model.addAttribute("pageTitle", "Đơn hàng của tôi");
        return "user/orders";
    }

    @GetMapping("/orders/{orderId}")
    public String orderDetail(@PathVariable Long orderId, Model model,
                               @AuthenticationPrincipal CustomUserDetails user) {
        model.addAttribute("order", orderService.getOrderDetail(user.getUserId(), orderId));
        model.addAttribute("cartCount", cartService.getCartItemCount(user.getUserId()));
        model.addAttribute("pageTitle", "Chi tiết đơn hàng");
        return "user/order-detail";
    }

    @GetMapping("/addresses")
    public String addresses(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        model.addAttribute("addresses", addressRepository.findByUserId(user.getUserId()));
        model.addAttribute("cartCount", cartService.getCartItemCount(user.getUserId()));
        model.addAttribute("pageTitle", "Địa chỉ của tôi");
        return "user/addresses";
    }

    @GetMapping("/wishlist")
    public String wishlist(@RequestParam(defaultValue = "0") int page, Model model,
                            @AuthenticationPrincipal CustomUserDetails user) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by("createdAt").descending());
        model.addAttribute("wishlist", wishlistRepository.findByUserId(user.getUserId(), pageable));
        model.addAttribute("cartCount", cartService.getCartItemCount(user.getUserId()));
        model.addAttribute("pageTitle", "Sản phẩm yêu thích");
        return "user/wishlist";
    }
}
