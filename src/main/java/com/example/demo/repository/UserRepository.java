package com.example.demo.repository;

import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<User> findByRole(UserRole role, Pageable pageable);

    @Query(value = "SELECT * FROM users u WHERE " +
    	       "(:keyword IS NULL OR LOWER(u.full_name::text) LIKE :keyword " +
    	       "OR LOWER(u.email::text) LIKE :keyword)",
    	       countQuery = "SELECT COUNT(*) FROM users u WHERE " +
    	       "(:keyword IS NULL OR LOWER(u.full_name::text) LIKE :keyword " +
    	       "OR LOWER(u.email::text) LIKE :keyword)",
    	       nativeQuery = true)
    	Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
}