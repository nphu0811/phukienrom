package com.example.demo.service.impl;
import com.example.demo.domain.entity.*;
import com.example.demo.domain.enums.*;
import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.response.*;
import com.example.demo.exception.*;
import com.example.demo.repository.*;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import com.example.demo.util.OrderCodeGenerator;
import com.example.demo.util.ProductVariantHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductVariantRepository variantRepository;
    private final CouponRepository couponRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Value("${app.order.cancel-window-hours:24}") private int cancelWindowHours;

    public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository,
                            ProductVariantRepository variantRepository, CouponRepository couponRepository,
                            AddressRepository addressRepository, UserRepository userRepository,
                            CartService cartService) {
        this.orderRepository = orderRepository; this.cartRepository = cartRepository;
        this.variantRepository = variantRepository; this.couponRepository = couponRepository;
        this.addressRepository = addressRepository; this.userRepository = userRepository;
        this.cartService = cartService;
    }

    @Override @Transactional
    public OrderResponse checkout(Long userId, CheckoutRequest request) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
            .orElseThrow(() -> new BusinessException("Giỏ hàng trống"));
        if (cart.getItems().isEmpty()) throw new BusinessException("Giỏ hàng trống");

        for (CartItem item : cart.getItems()) {
            if (item.getQuantity() > item.getVariant().getStock())
                throw new BusinessException("Sản phẩm '" + item.getVariant().getProduct().getName()
                    + "' chỉ còn " + item.getVariant().getStock() + " trong kho");
        }

        String recipientName, recipientPhone, shippingAddress;
        if (request.getAddressId() != null) {
            Address addr = addressRepository.findByIdAndUserId(request.getAddressId(), userId)
                .orElseThrow(() -> new BusinessException("Địa chỉ không tồn tại"));
            recipientName = addr.getRecipientName(); recipientPhone = addr.getPhone();
            shippingAddress = addr.getStreetAddress() + ", " + addr.getWard() + ", " + addr.getDistrict() + ", " + addr.getProvince();
        } else {
            recipientName = request.getRecipientName(); recipientPhone = request.getRecipientPhone();
            shippingAddress = request.getStreetAddress() + ", " + request.getWard() + ", " + request.getDistrict() + ", " + request.getProvince();
            if (request.isSaveAddress()) saveNewAddress(userId, request);
        }

        BigDecimal subtotal = cart.getTotalAmount();
        BigDecimal shippingFee = subtotal.compareTo(new BigDecimal("500000")) >= 0 ? BigDecimal.ZERO : new BigDecimal("30000");
        BigDecimal discountAmount = BigDecimal.ZERO;
        String couponCode = null;

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            Coupon coupon = couponRepository.findByCodeAndActiveTrue(request.getCouponCode())
                .orElseThrow(() -> new BusinessException("Mã giảm giá không hợp lệ"));
            if (!coupon.isValid()) throw new BusinessException("Mã giảm giá đã hết hạn");
            if (subtotal.compareTo(coupon.getMinOrderAmount()) < 0)
                throw new BusinessException("Đơn hàng tối thiểu " + coupon.getMinOrderAmount() + "₫");
            discountAmount = coupon.calculateDiscount(subtotal);
            couponCode = coupon.getCode();
            coupon.setUsedCount(coupon.getUsedCount() + 1);
        }

        BigDecimal totalAmount = subtotal.add(shippingFee).subtract(discountAmount);

        Order order = new Order();
        order.setOrderCode(OrderCodeGenerator.generate());
        order.setUser(userRepository.getReferenceById(userId));
        order.setStatus(OrderStatus.PENDING);
        order.setRecipientName(recipientName); order.setRecipientPhone(recipientPhone);
        order.setShippingAddress(shippingAddress);
        order.setSubtotal(subtotal); order.setShippingFee(shippingFee);
        order.setDiscountAmount(discountAmount); order.setTotalAmount(totalAmount);
        order.setPaymentMethod(request.getPaymentMethod()); order.setPaymentStatus(PaymentStatus.PENDING);
        order.setCouponCode(couponCode); order.setNote(request.getNote());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getVariant();
            int updated = variantRepository.decreaseStock(variant.getId(), cartItem.getQuantity());
            if (updated == 0) throw new BusinessException("Sản phẩm '" + variant.getProduct().getName() + "' vừa hết hàng");
            OrderItem oi = new OrderItem();
            oi.setOrder(order); oi.setVariant(variant); oi.setQuantity(cartItem.getQuantity());
            oi.setUnitPrice(cartItem.getUnitPrice()); oi.setProductName(variant.getProduct().getName());
            oi.setVariantInfo(ProductVariantHelper.buildVariantInfo(variant)); oi.setProductImage(variant.getProduct().getThumbnailUrl());
            orderItems.add(oi);
        }
        order.setItems(orderItems);
        Order saved = orderRepository.save(order);
        cartService.clearCart(userId);
        log.info("Order created: " + saved.getOrderCode());
        return toResponse(saved);
    }

    @Override @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).map(this::toResponse);
    }

    @Override @Transactional(readOnly = true)
    public OrderResponse getOrderDetail(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));
        return toResponse(order);
    }

    @Override @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId, String reason) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));
        if (!order.isCancellable()) throw new BusinessException("Đơn hàng không thể hủy ở trạng thái hiện tại");
        for (OrderItem item : order.getItems())
            variantRepository.increaseStock(item.getVariant().getId(), item.getQuantity());
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now()); order.setCancelReason(reason);
        return toResponse(orderRepository.save(order));
    }

    @Override @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(OrderStatus status, Pageable pageable) {
        if (status != null) return orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable).map(this::toResponse);
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    @Override @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        order.setStatus(newStatus);
        return toResponse(orderRepository.save(order));
    }

    private void saveNewAddress(Long userId, CheckoutRequest req) {
        User user = userRepository.getReferenceById(userId);
        Address addr = new Address();
        addr.setUser(user); addr.setRecipientName(req.getRecipientName());
        addr.setPhone(req.getRecipientPhone()); addr.setStreetAddress(req.getStreetAddress());
        addr.setWard(req.getWard()); addr.setDistrict(req.getDistrict()); addr.setProvince(req.getProvince());
        addressRepository.save(addr);
    }

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> items = new ArrayList<>();
        for (OrderItem i : o.getItems()) {
            OrderItemResponse ir = new OrderItemResponse();
            ir.setId(i.getId()); ir.setProductName(i.getProductName());
            ir.setVariantInfo(i.getVariantInfo()); ir.setProductImage(i.getProductImage());
            ir.setQuantity(i.getQuantity()); ir.setUnitPrice(i.getUnitPrice()); ir.setSubtotal(i.getSubtotal());
            items.add(ir);
        }
        OrderResponse r = new OrderResponse();
        r.setId(o.getId()); r.setOrderCode(o.getOrderCode()); r.setStatus(o.getStatus());
        r.setRecipientName(o.getRecipientName()); r.setRecipientPhone(o.getRecipientPhone());
        r.setShippingAddress(o.getShippingAddress()); r.setSubtotal(o.getSubtotal());
        r.setShippingFee(o.getShippingFee()); r.setDiscountAmount(o.getDiscountAmount());
        r.setTotalAmount(o.getTotalAmount()); r.setPaymentMethod(o.getPaymentMethod());
        r.setPaymentStatus(o.getPaymentStatus()); r.setCouponCode(o.getCouponCode());
        r.setNote(o.getNote()); r.setCancellable(o.isCancellable());
        r.setCreatedAt(o.getCreatedAt()); r.setItems(items);
        return r;
    }
}
