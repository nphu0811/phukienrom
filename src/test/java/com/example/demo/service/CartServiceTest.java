package com.example.demo.service;

import com.example.demo.domain.entity.*;
import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductVariantRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService — addItem()")
class CartServiceTest {

    @Mock CartRepository cartRepository;
    @Mock ProductVariantRepository variantRepository;
    @Mock UserRepository userRepository;

    @InjectMocks CartServiceImpl cartService;

    private Cart cart;
    private ProductVariant variant;
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("iPhone 15");
        product.setSlug("iphone-15");
        product.setBasePrice(new BigDecimal("20000000"));
        product.setThumbnailUrl("http://img.test/iphone.jpg");

        variant = new ProductVariant();
        variant.setId(10L);
        variant.setProduct(product);
        variant.setStock(5);
        variant.setActive(true);
        variant.setPrice(new BigDecimal("20000000"));
        variant.setSalePrice(null);

        cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>());

        User user = new User();
        user.setId(1L);
        cart.setUser(user);
    }

    @Test
    @DisplayName("add new item to empty cart → item created with correct price")
    void addNewItemToEmptyCart() {
        when(cartRepository.findByUserIdWithItems(1L)).thenReturn(Optional.of(cart));
        when(variantRepository.findById(10L)).thenReturn(Optional.of(variant));
        when(cartRepository.save(any())).thenReturn(cart);

        CartItemRequest req = new CartItemRequest();
        req.setVariantId(10L);
        req.setQuantity(2);

        CartResponse resp = cartService.addItem(1L, req);

        assertThat(resp).isNotNull();
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(cart.getItems().get(0).getUnitPrice()).isEqualByComparingTo("20000000");
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("add same variant twice → quantities merge")
    void addSameVariantMergesQuantity() {
        // Pre-existing cart item
        CartItem existing = new CartItem();
        existing.setId(100L);
        existing.setVariant(variant);
        existing.setQuantity(1);
        existing.setUnitPrice(new BigDecimal("20000000"));
        cart.getItems().add(existing);

        when(cartRepository.findByUserIdWithItems(1L)).thenReturn(Optional.of(cart));
        when(variantRepository.findById(10L)).thenReturn(Optional.of(variant));
        when(cartRepository.save(any())).thenReturn(cart);

        CartItemRequest req = new CartItemRequest();
        req.setVariantId(10L);
        req.setQuantity(2);

        cartService.addItem(1L, req);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(3); // 1 + 2
    }

    @Test
    @DisplayName("add quantity exceeding stock → throws BusinessException")
    void addExceedsStockThrows() {
        when(cartRepository.findByUserIdWithItems(1L)).thenReturn(Optional.of(cart));
        when(variantRepository.findById(10L)).thenReturn(Optional.of(variant));

        CartItemRequest req = new CartItemRequest();
        req.setVariantId(10L);
        req.setQuantity(10); // stock = 5

        assertThatThrownBy(() -> cartService.addItem(1L, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("kho");
    }

    @Test
    @DisplayName("add inactive variant → throws BusinessException")
    void addInactiveVariantThrows() {
        variant.setActive(false);
        when(cartRepository.findByUserIdWithItems(1L)).thenReturn(Optional.of(cart));
        when(variantRepository.findById(10L)).thenReturn(Optional.of(variant));

        CartItemRequest req = new CartItemRequest();
        req.setVariantId(10L);
        req.setQuantity(1);

        assertThatThrownBy(() -> cartService.addItem(1L, req))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("removeItem → item removed from cart")
    void removeItemSuccess() {
        CartItem item = new CartItem();
        item.setId(99L);
        item.setVariant(variant);
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("20000000"));
        cart.getItems().add(item);

        when(cartRepository.findByUserIdWithItems(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenReturn(cart);

        cartService.removeItem(1L, 99L);

        assertThat(cart.getItems()).isEmpty();
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("clearCart — all items removed")
    void clearCartSuccess() {
        CartItem item = new CartItem();
        item.setId(1L);
        item.setVariant(variant);
        item.setQuantity(1);
        item.setUnitPrice(BigDecimal.ONE);
        cart.getItems().add(item);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenReturn(cart);

        cartService.clearCart(1L);

        assertThat(cart.getItems()).isEmpty();
    }
}
