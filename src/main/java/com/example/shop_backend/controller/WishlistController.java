package com.example.shop_backend.controller;

import com.example.shop_backend.dto.request.WishlistRequest;
import com.example.shop_backend.dto.response.WishlistResponse;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.model.User;
import com.example.shop_backend.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * Thêm hoặc bỏ thích sản phẩm
     */
    @PostMapping("/toggle")
    public ApiResponse<String> toggleWishlist(@RequestBody @Valid WishlistRequest request,
                                              @AuthenticationPrincipal User user) {
        try {
            String result = wishlistService.toggleWishlist(request, user);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(2001, e.getMessage());
        }
    }

    /**
     * Lấy danh sách sản phẩm yêu thích của người dùng
     */
    @GetMapping
    public ApiResponse<List<WishlistResponse>> getUserWishlist(@AuthenticationPrincipal User user) {
        List<WishlistResponse> data = wishlistService.getUserWishlist(user);
        return ApiResponse.success("Danh sách sản phẩm yêu thích", data);
    }

    /**
     * Lấy danh sách sản phẩm được yêu thích nhiều nhất
     */
    @GetMapping("/top-liked")
    public ApiResponse<List<WishlistResponse>> getTopLikedProducts() {
        List<WishlistResponse> data = wishlistService.getTopLikedProducts();
        return ApiResponse.success("Sản phẩm yêu thích theo lượt thích giảm dần", data);
    }
}
