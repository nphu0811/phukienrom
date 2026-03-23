package com.example.demo.service.impl;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.UserRole;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expiration}") private long jwtExpiration;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new BusinessException("Email đã được sử dụng");

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);
        userRepository.save(user);
        log.info("New user registered: " + user.getEmail());

        CustomUserDetails userDetails = new CustomUserDetails(user);
        return buildAuthResponse(userDetails, user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getEmail()).orElseThrow();
        if (!user.isActive()) throw new BusinessException("Tài khoản đã bị khóa", 403);
        log.info("User logged in: " + user.getEmail());
        return buildAuthResponse(userDetails, user);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        try {
            String email = jwtUtil.extractEmail(refreshToken);
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Token không hợp lệ", 401));
            CustomUserDetails userDetails = new CustomUserDetails(user);
            if (!jwtUtil.isTokenValid(refreshToken, userDetails))
                throw new BusinessException("Refresh token hết hạn", 401);
            return buildAuthResponse(userDetails, user);
        } catch (BusinessException e) { throw e; }
        catch (Exception e) { throw new BusinessException("Refresh token không hợp lệ", 401); }
    }

    private AuthResponse buildAuthResponse(CustomUserDetails userDetails, User user) {
        UserResponse ur = new UserResponse();
        ur.setId(user.getId()); ur.setEmail(user.getEmail());
        ur.setFullName(user.getFullName()); ur.setPhone(user.getPhone());
        ur.setAvatarUrl(user.getAvatarUrl()); ur.setRole(user.getRole());

        AuthResponse resp = new AuthResponse();
        resp.setAccessToken(jwtUtil.generateAccessToken(userDetails));
        resp.setRefreshToken(jwtUtil.generateRefreshToken(userDetails));
        resp.setTokenType("Bearer");
        resp.setExpiresIn(jwtExpiration / 1000);
        resp.setUser(ur);
        return resp;
    }
}
