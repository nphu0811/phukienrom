package com.example.demo.service.impl;
import com.example.demo.domain.entity.*;
import com.example.demo.dto.request.ReviewRequest;
import com.example.demo.dto.response.ReviewResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndApprovedTrue(productId, pageable).map(this::toResponse);
    }

    @Transactional
    public ReviewResponse createReview(Long userId, ReviewRequest request) {
        if (reviewRepository.existsByProductIdAndUserId(request.getProductId(), userId))
            throw new BusinessException("Bạn đã đánh giá sản phẩm này rồi");

        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));
        User user = userRepository.getReferenceById(userId);

        Review review = new Review();
        review.setProduct(product); review.setUser(user);
        review.setRating(request.getRating()); review.setComment(request.getComment());
        review.setApproved(true);
        reviewRepository.save(review);

        Double avg = reviewRepository.getAvgRating(product.getId());
        int count = reviewRepository.getReviewCount(product.getId());
        product.setAvgRating(avg != null ? BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        product.setReviewCount(count);
        productRepository.save(product);
        return toResponse(review);
    }

    private ReviewResponse toResponse(Review r) {
        ReviewResponse resp = new ReviewResponse();
        resp.setId(r.getId()); resp.setUserName(r.getUser().getFullName());
        resp.setRating(r.getRating()); resp.setComment(r.getComment());
        resp.setReply(r.getReply()); resp.setCreatedAt(r.getCreatedAt());
        return resp;
    }
}
