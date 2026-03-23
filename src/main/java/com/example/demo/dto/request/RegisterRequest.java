package com.example.demo.dto.request;
import jakarta.validation.constraints.*;

public class RegisterRequest {
    @NotBlank(message = "Họ tên không được để trống") @Size(min = 2, max = 100)
    private String fullName;
    @NotBlank(message = "Email không được để trống") @Email(message = "Email không hợp lệ")
    private String email;
    @NotBlank(message = "Mật khẩu không được để trống") @Size(min = 6, max = 100)
    private String password;
    @Size(max = 20) private String phone;

    public RegisterRequest() {}
    public String getFullName() { return fullName; } public void setFullName(String v) { this.fullName = v; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
}
