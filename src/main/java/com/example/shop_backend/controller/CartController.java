package com.example.shop_backend.controller;

import com.example.shop_backend.dto.request.CartItemRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.CartResponse;
import com.example.shop_backend.model.User;
import com.example.shop_backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/add")
    public ApiResponse<CartResponse> addToCart(
            @AuthenticationPrincipal User user,
            @RequestBody CartItemRequest request
    ) {
        return ApiResponse.success(
                "Thêm vào giỏ hàng thành công",
                cartService.addToCart(request, user)
        );
    }

    // Cập nhật số lượng
    @PutMapping("/update/{cartItemId}")
    public ApiResponse<CartResponse> updateCartItem(
            @AuthenticationPrincipal User user,
            @PathVariable Integer cartItemId,
            @RequestParam Integer quantity
    ) {
        return ApiResponse.success(
                "Cập nhật giỏ hàng thành công",
                cartService.updateCartItem(cartItemId, quantity, user)
        );
    }

    // Xóa 1 item
    @DeleteMapping("/remove/{cartItemId}")
    public ApiResponse<CartResponse> removeCartItem(
            @AuthenticationPrincipal User user,
            @PathVariable Integer cartItemId
    ) {
        return ApiResponse.success(
                "Đã xóa sản phẩm khỏi giỏ hàng",
                cartService.removeCartItem(cartItemId, user)
        );
    }

    // Xóa toàn bộ giỏ hàng
    @DeleteMapping("/clear")
    public ApiResponse<Void> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ApiResponse.success("Đã xóa toàn bộ giỏ hàng", null);
    }

    // Lấy giỏ hàng của user
    @GetMapping
    public ApiResponse<CartResponse> getCart(@AuthenticationPrincipal User user) {
        return ApiResponse.success(
                "Lấy giỏ hàng thành công",
                cartService.getCart(user)
        );
    }
}
