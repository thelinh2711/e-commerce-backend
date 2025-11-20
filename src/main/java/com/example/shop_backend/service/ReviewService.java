package com.example.shop_backend.service;

import com.example.shop_backend.dto.request.CreateReviewRequest;
import com.example.shop_backend.dto.response.PageResponse;
import com.example.shop_backend.dto.response.ReviewPageResponse;
import com.example.shop_backend.dto.response.ReviewResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.mapper.ReviewMapper;
import com.example.shop_backend.model.*;
import com.example.shop_backend.model.enums.OrderStatus;
import com.example.shop_backend.repository.OrderRepository;
import com.example.shop_backend.repository.ProductVariantRepository;
import com.example.shop_backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ReviewMapper reviewMapper;

    // ========================
    // Tạo review
    // ========================
    @Transactional
    public ReviewResponse create(CreateReviewRequest request, User currentUser) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (!order.getStatus().equals(OrderStatus.DELIVERED)) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }

        ProductVariant variant = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));

        boolean variantInOrder = order.getItems().stream()
                .anyMatch(item -> item.getProductVariant().getId().equals(variant.getId()));

        if (!variantInOrder) {
            throw new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND);
        }

        boolean alreadyReviewed = reviewRepository.existsByUserIdAndProductVariantIdAndOrderId(
                currentUser.getId(),
                variant.getId(),
                order.getId()
        );
        if (alreadyReviewed) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Review review = new Review();
        review.setUser(currentUser);
        review.setOrder(order);
        review.setProduct(variant.getProduct());
        review.setProductVariant(variant);
        review.setRating(request.getRating());
        review.setTitle(getTitleByRating(request.getRating()));
        review.setComment(request.getComment());

        Review saved = reviewRepository.save(review);
        return reviewMapper.toReviewResponse(saved);
    }

    private String getTitleByRating(int rating) {
        return switch (rating) {
            case 5 -> "Rất hài lòng";
            case 4 -> "Hài lòng";
            case 3 -> "Bình thường";
            case 2 -> "Không hài lòng";
            case 1 -> "Rất không hài lòng";
            default -> "Không xác định";
        };
    }

    // ========================
    // Xóa review (chỉ admin)
    // ========================
    @Transactional
    public void delete(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));
        reviewRepository.delete(review);
    }

    // Lấy review theo productId (phân trang)
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getByProductIdWithAvgRating(Integer productId, Pageable pageable) {

        // Lấy trang review
        Page<Review> page = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
        List<ReviewResponse> list = page.stream()
                .map(reviewMapper::toReviewResponse)
                .toList();

        // Tính rating trung bình
        Double average = reviewRepository.findAverageRatingByProductId(productId);
        if (average == null) average = 0.0;

        // Làm tròn 1 chữ số thập phân
        average = Math.round(average * 10) / 10.0;

        return PageResponse.<ReviewResponse>builder()
                .data(list)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .extra(Map.of("averageRating", average)) // Thêm rating trung bình vào response
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getByProductIdAndRating(Integer productId, Integer rating, Pageable pageable) {

        Page<Review> page = reviewRepository.findByProductIdAndRatingOrderByCreatedAtDesc(productId, rating, pageable);
        List<ReviewResponse> list = page.stream()
                .map(reviewMapper::toReviewResponse)
                .toList();

        return PageResponse.<ReviewResponse>builder()
                .data(list)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }


    // ========================
    // Kiểm tra sản phẩm đã review chưa
    // ========================
    @Transactional(readOnly = true)
    public boolean isReviewed(Integer userId, Integer variantId, Integer orderId) {
        return reviewRepository.existsByUserIdAndProductVariantIdAndOrderId(userId, variantId, orderId);
    }
}
