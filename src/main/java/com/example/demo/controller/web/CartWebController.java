package com.example.demo.controller.web;
import com.example.demo.repository.AddressRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartWebController {
    private final CartService cartService;
    private final AddressRepository addressRepository;

    public CartWebController(CartService cartService, AddressRepository addressRepository) {
        this.cartService = cartService; this.addressRepository = addressRepository;
    }

    @GetMapping("/cart")
    public String cart(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        if (user != null) {
            model.addAttribute("cart", cartService.getCart(user.getUserId()));
            model.addAttribute("cartCount", cartService.getCartItemCount(user.getUserId()));
        }
        model.addAttribute("pageTitle", "Giỏ hàng");
        return "cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        if (user == null) return "redirect:/auth/login?redirect=/checkout";
        model.addAttribute("cart", cartService.getCart(user.getUserId()));
        model.addAttribute("addresses", addressRepository.findByUserId(user.getUserId()));
        model.addAttribute("cartCount", cartService.getCartItemCount(user.getUserId()));
        model.addAttribute("pageTitle", "Thanh toán");
        return "checkout";
    }
}
