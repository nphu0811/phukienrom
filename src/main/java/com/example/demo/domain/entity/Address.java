package com.example.demo.domain.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "addresses", indexes = { @Index(name = "idx_addresses_user", columnList = "user_id") })
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(name = "recipient_name", nullable = false, length = 100) private String recipientName;
    @Column(nullable = false, length = 20) private String phone;
    @Column(name = "street_address", nullable = false, length = 255) private String streetAddress;
    @Column(nullable = false, length = 100) private String ward;
    @Column(nullable = false, length = 100) private String district;
    @Column(nullable = false, length = 100) private String province;
    @Column(name = "is_default") private boolean defaultAddress = false;

    public Address() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public User getUser() { return user; } public void setUser(User user) { this.user = user; }
    public String getRecipientName() { return recipientName; } public void setRecipientName(String v) { this.recipientName = v; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public String getStreetAddress() { return streetAddress; } public void setStreetAddress(String v) { this.streetAddress = v; }
    public String getWard() { return ward; } public void setWard(String ward) { this.ward = ward; }
    public String getDistrict() { return district; } public void setDistrict(String v) { this.district = v; }
    public String getProvince() { return province; } public void setProvince(String v) { this.province = v; }
    public boolean isDefaultAddress() { return defaultAddress; } public void setDefaultAddress(boolean v) { this.defaultAddress = v; }
}
