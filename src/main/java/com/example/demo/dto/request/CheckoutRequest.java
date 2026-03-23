package com.example.demo.dto.request;
import com.example.demo.domain.enums.PaymentMethod;
import jakarta.validation.constraints.*;

public class CheckoutRequest {
    private Long addressId;
    @NotBlank(message = "Tên người nhận không được để trống") private String recipientName;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{8,10}$", message = "Số điện thoại không hợp lệ")
    private String recipientPhone;
    @NotBlank(message = "Địa chỉ không được để trống") private String streetAddress;
    @NotBlank private String ward;
    @NotBlank private String district;
    @NotBlank private String province;
    @NotNull(message = "Phương thức thanh toán không được để trống") private PaymentMethod paymentMethod;
    @Size(max = 50, message = "Mã giảm giá không hợp lệ")
    private String couponCode;
    @Size(max = 500, message = "Ghi chú tối đa 500 ký tự")
    private String note;
    private boolean saveAddress = false;

    public CheckoutRequest() {}
    public Long getAddressId() { return addressId; } public void setAddressId(Long v) { this.addressId = v; }
    public String getRecipientName() { return recipientName; } public void setRecipientName(String v) { this.recipientName = v; }
    public String getRecipientPhone() { return recipientPhone; } public void setRecipientPhone(String v) { this.recipientPhone = v; }
    public String getStreetAddress() { return streetAddress; } public void setStreetAddress(String v) { this.streetAddress = v; }
    public String getWard() { return ward; } public void setWard(String ward) { this.ward = ward; }
    public String getDistrict() { return district; } public void setDistrict(String v) { this.district = v; }
    public String getProvince() { return province; } public void setProvince(String v) { this.province = v; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; } public void setPaymentMethod(PaymentMethod v) { this.paymentMethod = v; }
    public String getCouponCode() { return couponCode; } public void setCouponCode(String v) { this.couponCode = v; }
    public String getNote() { return note; } public void setNote(String note) { this.note = note; }
    public boolean isSaveAddress() { return saveAddress; } public void setSaveAddress(boolean v) { this.saveAddress = v; }
}
