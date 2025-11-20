package com.example.shop_backend.controller;

import com.example.shop_backend.dto.request.CreateReviewRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.ReviewPageResponse;
import com.example.shop_backend.dto.response.ReviewResponse;
import com.example.shop_backend.model.User;
import com.example.shop_backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // ========================
    // Tạo review (customer)
    // ========================
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<ReviewResponse> createReview(
            @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal User currentUser) {

        // request hiện tại phải có productVariantId
        ReviewResponse response = reviewService.create(request, currentUser);
        return ApiResponse.success("Đánh giá thành công", response);
    }

    // ========================
    // Xóa review (admin)
    // ========================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteReview(@PathVariable Integer id) {
        reviewService.delete(id);
        return ApiResponse.success("Xóa đánh giá thành công", null);
    }

    // ========================
    // Lấy review theo productId (có phân trang, optional rating)
    // ========================
    @GetMapping("/product/{productId}")
    public ApiResponse<?> getReviewsByProduct(
            @PathVariable Integer productId,
            Pageable pageable,
            @RequestParam(required = false) Integer rating) {

        if (rating != null) {
            // Lọc review theo rating, trả về PageResponse<ReviewResponse>
            return ApiResponse.success(reviewService.getByProductIdAndRating(productId, rating, pageable));
        } else {
            // Lấy tất cả review + tính trung bình
            return ApiResponse.success(reviewService.getByProductIdWithAvgRating(productId, pageable));
        }
    }
}
