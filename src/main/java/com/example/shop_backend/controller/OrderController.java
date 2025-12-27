package com.example.shop_backend.controller;

import com.example.shop_backend.dto.request.CreateOrderRequest;
import com.example.shop_backend.dto.request.UpdateOrderStatusRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.OrderListResponse;
import com.example.shop_backend.dto.response.OrderResponse;
import com.example.shop_backend.dto.response.PageResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.model.User;
import com.example.shop_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Tạo đơn hàng (mua ngay hoặc từ cart)
     * Chỉ CUSTOMER đã đăng nhập mới được phép
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal User currentUser,
            @Validated @RequestBody CreateOrderRequest request) {

        // Lấy cartItemIds trực tiếp từ request body
        List<Integer> cartItemIds = request.getCartItemIds();

        OrderResponse orderResponse = orderService.createOrder(currentUser, request, cartItemIds);
        return ResponseEntity.ok(ApiResponse.success(orderResponse));
    }

    /**
     * Lấy danh sách đơn hàng của người dùng đang đăng nhập
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal User currentUser) {

        List<OrderResponse> orders = orderService.getOrdersByUser(currentUser);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    /**
     * Lấy chi tiết đơn hàng theo ID (chỉ user sở hữu đơn hàng hoặc admin)
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer orderId) {

        OrderResponse orderResponse = orderService.getOrderDetail(currentUser, orderId);
        return ResponseEntity.ok(ApiResponse.success(orderResponse));
    }

    /**
     * Tracking đơn hàng (chỉ admin)
     */
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Integer orderId, @RequestBody UpdateOrderStatusRequest request
            ) {
        OrderResponse updateOrder = orderService.updateOrderStatus(orderId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success(updateOrder));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<PageResponse<OrderListResponse>>> searchOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : null;

        PageResponse<OrderListResponse> result =
                orderService.searchOrders(
                        keyword,
                        status,
                        fromDateTime,
                        toDateTime,
                        pageable
                );

        return ResponseEntity.ok(ApiResponse.success(result));
    }

}
