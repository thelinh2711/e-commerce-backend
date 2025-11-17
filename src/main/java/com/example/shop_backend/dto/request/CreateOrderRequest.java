package com.example.shop_backend.dto.request;

import com.example.shop_backend.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull(message = "Phương thức thanh toán không được để trống")
    private PaymentMethod paymentMethod;

    @NotEmpty(message = "Địa chỉ không được để trống")
    private String address;

    @NotEmpty(message = "Họ tên người nhận không được để trống")
    private String fullName;

    @NotEmpty(message = "Số điện thoại không được để trống")
    private String phone;

    private String note;

    // Reward points the user muốn dùng
    private Integer rewardPointsToUse;

    // Nếu mua ngay 1 sản phẩm
    private BuyNowItem buyNowItem;

    // Nếu mua từ giỏ hàng
    private List<Integer> cartItemIds;

    // Danh sách voucher
    private List<String> voucherCodes;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BuyNowItem {
        @NotNull(message = "Product variant id không được để trống")
        private Integer productVariantId;

        @NotNull(message = "Số lượng không được để trống")
        private Integer quantity;
    }
}

