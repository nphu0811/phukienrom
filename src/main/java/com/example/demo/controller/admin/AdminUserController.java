package com.example.demo.controller.admin;

import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.UserRole;
import com.example.demo.dto.request.UpdatePasswordRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<ApiResponse<User>> updateRole(@PathVariable Long id,
                                                        @RequestParam UserRole role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(role);
        return ResponseEntity.ok(ApiResponse.success("Đã cập nhật vai trò", userRepository.save(user)));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<User>> toggleActive(@PathVariable Long id,
                                                          @RequestParam boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setActive(active);
        return ResponseEntity.ok(ApiResponse.success(active ? "Đã mở khóa" : "Đã khóa tài khoản",
                userRepository.save(user)));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@PathVariable Long id,
                                                            @Valid @RequestBody UpdatePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Đã đổi mật khẩu", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(404).body(ApiResponse.error(404, "User not found"));
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa người dùng", null));
    }
}
