package com.example.shop_backend.controller;

import com.example.shop_backend.dto.request.AddressRequest;
import com.example.shop_backend.dto.response.AddressResponse;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.model.User;
import com.example.shop_backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    // 🟢 Lấy tất cả địa chỉ của user hiện tại
    @GetMapping
    public ApiResponse<List<AddressResponse>> getAddresses(@AuthenticationPrincipal User user) {
        return ApiResponse.<List<AddressResponse>>builder()
                .result(addressService.getAddresses(user))
                .build();
    }

    // 🟢 Lấy địa chỉ theo id
    @GetMapping("/{id}")
    public ApiResponse<AddressResponse> getAddressById(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.getAddressById(id, user))
                .build();
    }

    // 🟢 Tạo địa chỉ mới
    @PostMapping
    public ApiResponse<AddressResponse> createAddress(@RequestBody AddressRequest request,
                                                      @AuthenticationPrincipal User user) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.createAddress(request, user))
                .build();
    }

    // 🟡 Cập nhật địa chỉ
    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> updateAddress(@PathVariable Integer id,
                                                      @RequestBody AddressRequest request,
                                                      @AuthenticationPrincipal User user) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.updateAddress(id, request, user))
                .build();
    }

    // 🔴 Xóa địa chỉ
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        addressService.deleteAddress(id, user);
        return ApiResponse.<Void>builder().message("Address deleted successfully").build();
    }
}
