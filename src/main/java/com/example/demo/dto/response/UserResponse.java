package com.example.demo.dto.response;
import com.example.demo.domain.enums.UserRole;
import java.time.LocalDateTime;

public class UserResponse {
    private Long id; private String email; private String fullName;
    private String phone; private String avatarUrl; private UserRole role;
    private boolean active; private LocalDateTime createdAt;

    public UserResponse() {}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; } public void setFullName(String v) { this.fullName = v; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public String getAvatarUrl() { return avatarUrl; } public void setAvatarUrl(String v) { this.avatarUrl = v; }
    public UserRole getRole() { return role; } public void setRole(UserRole role) { this.role = role; }
    public boolean isActive() { return active; } public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}
