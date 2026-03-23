package com.example.demo.security;

import com.example.demo.domain.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String email;
    private final String password;
    private final String role;
    private final boolean active;

    public CustomUserDetails(User user) {
        this.userId   = user.getId();
        this.email    = user.getEmail();
        this.password = user.getPassword();
        this.role     = user.getRole().name();
        this.active   = user.isActive();
    }

    public Long getUserId() { return userId; }
    public String getEmail()  { return email; }   // <-- thêm lại getEmail()
    public String getRole()   { return role; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override public String getPassword()              { return password; }
    @Override public String getUsername()              { return email; }   // Spring Security dùng getUsername()
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return active; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return active; }
}