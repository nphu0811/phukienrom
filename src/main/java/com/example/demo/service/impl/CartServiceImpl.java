package com.example.demo.service.impl;
import com.example.demo.domain.entity.*;
import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.response.*;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import com.example.demo.service.CartService;
import com.example.demo.util.ProductVariantHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           ProductVariantRepository variantRepository,
                           UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.variantRepository = variantRepository;
        this.userRepository = userRepository;
    }

    @Override @Transactional
    public CartResponse getCart(Long userId) { return toResponse(getOrCreateCart(userId)); }

    @Override @Transactional
    public CartResponse addItem(Long userId, CartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        ProductVariant variant = variantRepository.findById(request.getVariantId())
            .orElseThrow(() -> new ResourceNotFoundException("Variant", request.getVariantId()));
        if (!variant.isActive()) throw new BusinessException("Sản phẩm này hiện không có sẵn");

        Optional<CartItem> existingItem = cart.getItems().stream()
            .filter(i -> i.getVariant().getId().equals(request.getVariantId())).findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQty = item.getQuantity() + request.getQuantity();
            if (newQty > variant.getStock())
                throw new BusinessException("Chỉ còn " + variant.getStock() + " sản phẩm trong kho");
            item.setQuantity(newQty);
            item.setUnitPrice(variant.getEffectivePrice());
        } else {
            if (request.getQuantity() > variant.getStock())
                throw new BusinessException("Chỉ còn " + variant.getStock() + " sản phẩm trong kho");
            CartItem newItem = new CartItem();
            newItem.setCart(cart); newItem.setVariant(variant);
            newItem.setQuantity(request.getQuantity()); newItem.setUnitPrice(variant.getEffectivePrice());
            cart.getItems().add(newItem);
        }
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Override @Transactional
    public CartResponse updateItem(Long userId, Long itemId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cart.getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Cart item", itemId));
        if (quantity <= 0) { cart.getItems().remove(item); }
        else {
            if (quantity > item.getVariant().getStock())
                throw new BusinessException("Chỉ còn " + item.getVariant().getStock() + " sản phẩm trong kho");
            item.setQuantity(quantity);
        }
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Override @Transactional
    public CartResponse removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().removeIf(i -> i.getId().equals(itemId));
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Override @Transactional
    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.getItems().clear(); cartRepository.save(cart);
        });
    }

    @Override @Transactional(readOnly = true)
    public int getCartItemCount(Long userId) {
        return cartRepository.findByUserId(userId).map(Cart::getTotalItems).orElse(0);
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserIdWithItems(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
            Cart newCart = new Cart(); newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            Product product = item.getVariant().getProduct();
            CartItemResponse r = new CartItemResponse();
            r.setId(item.getId()); r.setVariantId(item.getVariant().getId());
            r.setProductName(product.getName()); r.setVariantInfo(ProductVariantHelper.buildVariantInfo(item.getVariant()));
            r.setProductImage(product.getThumbnailUrl()); r.setProductSlug(product.getSlug());
            r.setQuantity(item.getQuantity()); r.setUnitPrice(item.getUnitPrice());
            r.setSubtotal(item.getSubtotal()); r.setAvailableStock(item.getVariant().getStock());
            items.add(r);
        }
        CartResponse resp = new CartResponse();
        resp.setCartId(cart.getId()); resp.setItems(items);
        resp.setTotalItems(cart.getTotalItems()); resp.setTotalAmount(cart.getTotalAmount());
        return resp;
    }
}
