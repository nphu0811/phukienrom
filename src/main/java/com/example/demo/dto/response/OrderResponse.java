package com.example.demo.dto.response;
import com.example.demo.domain.enums.OrderStatus;
import com.example.demo.domain.enums.PaymentMethod;
import com.example.demo.domain.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Long id; private String orderCode; private OrderStatus status;
    private String recipientName; private String recipientPhone; private String shippingAddress;
    private BigDecimal subtotal; private BigDecimal shippingFee; private BigDecimal discountAmount;
    private BigDecimal totalAmount; private PaymentMethod paymentMethod; private PaymentStatus paymentStatus;
    private String couponCode; private String note; private boolean cancellable;
    private LocalDateTime createdAt; private List<OrderItemResponse> items;

    public OrderResponse() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getOrderCode() { return orderCode; } public void setOrderCode(String v) { this.orderCode = v; }
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
    public boolean isCancellable() { return cancellable; } public void setCancellable(boolean cancellable) { this.cancellable = cancellable; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public List<OrderItemResponse> getItems() { return items; } public void setItems(List<OrderItemResponse> items) { this.items = items; }
}
