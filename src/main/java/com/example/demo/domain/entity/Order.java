package com.example.demo.domain.entity;
import com.example.demo.domain.enums.OrderStatus;
import com.example.demo.domain.enums.PaymentMethod;
import com.example.demo.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_user", columnList = "user_id"),
    @Index(name = "idx_orders_status", columnList = "status"),
    @Index(name = "idx_orders_code", columnList = "order_code", unique = true),
    @Index(name = "idx_orders_created_at", columnList = "created_at")
})
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "order_code", nullable = false, unique = true, length = 20) private String orderCode;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private OrderStatus status = OrderStatus.PENDING;
    @Column(name = "recipient_name", nullable = false, length = 100) private String recipientName;
    @Column(name = "recipient_phone", nullable = false, length = 20) private String recipientPhone;
    @Column(name = "shipping_address", nullable = false, length = 500) private String shippingAddress;
    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2) private BigDecimal subtotal;
    @Column(name = "shipping_fee", precision = 15, scale = 2) private BigDecimal shippingFee = BigDecimal.ZERO;
    @Column(name = "discount_amount", precision = 15, scale = 2) private BigDecimal discountAmount = BigDecimal.ZERO;
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2) private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING) @Column(name = "payment_method", nullable = false) private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING) @Column(name = "payment_status") private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    @Column(name = "coupon_code", length = 50) private String couponCode;
    @Column(name = "note", columnDefinition = "TEXT") private String note;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @Column(name = "cancelled_at") private LocalDateTime cancelledAt;
    @Column(name = "cancel_reason", length = 500) private String cancelReason;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) private List<OrderItem> items = new ArrayList<>();

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public boolean isCancellable() { return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED; }

    public Order() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getOrderCode() { return orderCode; } public void setOrderCode(String v) { this.orderCode = v; }
    public User getUser() { return user; } public void setUser(User user) { this.user = user; }
    public OrderStatus getStatus() { return status; } public void setStatus(OrderStatus v) { this.status = v; }
    public String getRecipientName() { return recipientName; } public void setRecipientName(String v) { this.recipientName = v; }
    public String getRecipientPhone() { return recipientPhone; } public void setRecipientPhone(String v) { this.recipientPhone = v; }
    public String getShippingAddress() { return shippingAddress; } public void setShippingAddress(String v) { this.shippingAddress = v; }
    public BigDecimal getSubtotal() { return subtotal; } public void setSubtotal(BigDecimal v) { this.subtotal = v; }
    public BigDecimal getShippingFee() { return shippingFee; } public void setShippingFee(BigDecimal v) { this.shippingFee = v; }
    public BigDecimal getDiscountAmount() { return discountAmount; } public void setDiscountAmount(BigDecimal v) { this.discountAmount = v; }
    public BigDecimal getTotalAmount() { return totalAmount; } public void setTotalAmount(BigDecimal v) { this.totalAmount = v; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; } public void setPaymentMethod(PaymentMethod v) { this.paymentMethod = v; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; } public void setPaymentStatus(PaymentStatus v) { this.paymentStatus = v; }
    public String getCouponCode() { return couponCode; } public void setCouponCode(String v) { this.couponCode = v; }
    public String getNote() { return note; } public void setNote(String note) { this.note = note; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; } public void setCancelledAt(LocalDateTime v) { this.cancelledAt = v; }
    public String getCancelReason() { return cancelReason; } public void setCancelReason(String v) { this.cancelReason = v; }
    public List<OrderItem> getItems() { return items; } public void setItems(List<OrderItem> items) { this.items = items; }
}
