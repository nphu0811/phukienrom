package com.example.demo.service;

import com.example.demo.domain.entity.*;
import com.example.demo.domain.enums.OrderStatus;
import com.example.demo.domain.enums.PaymentMethod;
import com.example.demo.domain.enums.PaymentStatus;
import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import com.example.demo.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService — checkout()")
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock CartRepository cartRepository;
    @Mock ProductVariantRepository variantRepository;
    @Mock CouponRepository couponRepository;
    @Mock AddressRepository addressRepository;
    @Mock UserRepository userRepository;
    @Mock CartService cartService;

    @InjectMocks OrderServiceImpl orderService;

    private Cart cart;
    private ProductVariant variant;
    private CartItem cartItem;
    private CheckoutRequest request;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Samsung Galaxy S24");
        product.setThumbnailUrl("http://img.test/s24.jpg");
        product.setBasePrice(new BigDecimal("15000000"));

        variant = new ProductVariant();
        variant.setId(10L);
        variant.setProduct(product);
        variant.setStock(10);
        variant.setActive(true);
        variant.setPrice(new BigDecimal("15000000"));

        cartItem = new CartItem();
        cartItem.setVariant(variant);
        cartItem.setQuantity(1);
        cartItem.setUnitPrice(new BigDecimal("15000000"));

        cart = new Cart();
        cart.setId(1L);
        List<CartItem> items = new ArrayList<>();
        items.add(cartItem);
        cart.setItems(items);

        request = new CheckoutRequest();
        request.setRecipientName("Nguyen Van A");
        request.setRecipientPhone("0901234567");
        request.setStreetAddress("123 Le Loi");
        request.setWard("Ben Nghe");
        request.setDistrict("Quan 1");
        request.setProvince("TP.HCM");
        request.setPaymentMethod(PaymentMethod.COD);
    }

    @Test
    @DisplayName("successful checkout → order created, cart cleared")
    void checkoutSuccess() {
        when(cartRepository.findByUserIdWithItems(1L)).thenReturn(Optional.of(cart));
        when(variantRepository.decreaseStock(eq(10L), eq(1))).thenReturn(1);
        when(userRepository.getReferenceById(1L)).thenReturn(new User());

        Order savedOrder = new Order();
        savedOrder.setId(100L);
        savedOrder.setOrderCode("ORD20240101001");
        savedOrder.setStatus(OrderStatus.PENDING);
        savedOrder.setRecipientName("Nguyen Van A");
        savedOrder.setRecipientPhone("0901234567");
        savedOrder.setShippingAddress("123 Le Loi, Ben Nghe, Quan 1, TP.HCM");
        savedOrder.setSubtotal(new BigDecimal("15000000"));
        savedOrder.setShippingFee(BigDecimal.ZERO); // free ship > 500k
        savedOrder.setDiscountAmount(BigDecimal.ZERO);
        savedOrder.setTotalAmount(new BigDecimal("15000000"));
        savedOrder.setPaymentMethod(PaymentMethod.COD);
        savedOrder.setPaymentStatus(PaymentStatus.PENDING);
        savedOrder.setItems(new ArrayList<>());

        when(orderRepository.save(any())).thenReturn(savedOrder);

        OrderResponse response = orderService.checkout(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(variantRepository).decreaseStock(10L, 1);
        verify(cartService).clearCart(1L);
    }

    @Test
    @DisplayName("empty cart → throws BusinessException")
    void emptyCartThrows() {
        Cart emptyCart = new Cart();
        emptyCart.setItems(new ArrayList<>());
        when(cartRepository.findByUserIdWithItems(1L)).thenReturn(Optional.of(emptyCart));

        assertThatThrownBy(() -> orderService.checkout(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("trống");
    }

    @Test
    @DisplayName("cart not found → throws BusinessException")
    void cartNotFoundThrows() {
        when(cartRepository.findByUserIdWithItems(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.checkout(1L, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("item quantity exceeds stock → throws BusinessException")
    void stockExceededThrows() {
        cartItem.setQuantity(100); // way more than stock=10
        when(cartRepository.findByUserIdWithItems(1L)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> orderService.checkout(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("kho");
    }

    @Test
    @DisplayName("invalid coupon code → throws BusinessException")
    void invalidCouponThrows() {
        request.setCouponCode("INVALID");
        when(cartRepository.findByUserIdWithItems(1L)).thenReturn(Optional.of(cart));
        when(couponRepository.findByCodeAndActiveTrue("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.checkout(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("giảm giá");
    }

    @Test
    @DisplayName("cancel PENDING order → status becomes CANCELLED")
    void cancelPendingOrder() {
        Order order = new Order();
        order.setId(50L);
        order.setStatus(OrderStatus.PENDING);
        order.setItems(new ArrayList<>());

        when(orderRepository.findByIdAndUserId(50L, 1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        OrderResponse response = orderService.cancelOrder(1L, 50L, "Changed mind");

        assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("cancel COMPLETED order → throws BusinessException")
    void cancelCompletedOrderThrows() {
        Order order = new Order();
        order.setId(50L);
        order.setStatus(OrderStatus.COMPLETED);
        order.setItems(new ArrayList<>());

        when(orderRepository.findByIdAndUserId(50L, 1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 50L, "too late"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("không thể hủy");
    }

    @Test
    @DisplayName("get order of different user → throws ResourceNotFoundException")
    void getOrderWrongUserThrows() {
        when(orderRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderDetail(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
