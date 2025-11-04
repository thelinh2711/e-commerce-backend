package com.example.shop_backend.controller;

import com.example.shop_backend.dto.request.AddressRequest;
import com.example.shop_backend.dto.response.AddressResponse;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.model.User;
import com.example.shop_backend.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ApiResponse<List<AddressResponse>> getAddresses(@AuthenticationPrincipal User user) {
        return ApiResponse.success(addressService.getAddresses(user));
    }

    @GetMapping("/{id}")
    public ApiResponse<AddressResponse> getAddress(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        return ApiResponse.success(addressService.getAddressById(id, user));
    }

    @PostMapping
    public ApiResponse<AddressResponse> createAddress(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal User user) {
        return ApiResponse.success(addressService.createAddress(request, user));
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> updateAddress(
            @PathVariable Integer id,
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal User user) {
        return ApiResponse.success(addressService.updateAddress(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        addressService.deleteAddress(id, user);
        return ApiResponse.success(null);
    }
}
