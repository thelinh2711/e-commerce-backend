package com.example.shop_backend.dto.response;

import com.example.shop_backend.model.enums.OrderStatus;
import com.example.shop_backend.model.enums.PaymentMethod;
import com.example.shop_backend.model.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Integer id;
    private String fullName;
    private String phone;
    private String address;
    private String note;
    private PaymentMethod paymentMethod;
    private OrderStatus status;

    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;          // sau giảm
    private BigDecimal shippingDiscount;     // voucher giảm
    private BigDecimal shippingFeeOriginal;  // phí ship gốc
    private BigDecimal totalAmount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<OrderItemResponse> items;

    // Reward points fields
    private Integer rewardPointsUsed;
    private Integer rewardPointsEarned;
    private Integer userRemainingRewardPoints;

    // Payment information
    private PaymentResponse payment;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Integer id;

        // Thông tin biến thể
        private Integer productVariantId;
        private String productSku;
        private String productName; // tên sản phẩm
        private String color;       // tên màu
        private String size;        // tên size
        private Integer quantity;
        private BigDecimal unitPrice;   // giá bán tại thời điểm mua
        private BigDecimal totalPrice;  // unitPrice * quantity
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentResponse {
        private Integer id;
        private PaymentStatus status;           // PAID, UNPAID, FAILED
        private PaymentMethod paymentMethod;    // COD, BANK_TRANSFER, VNPAY
        private BigDecimal amount;
        private String transactionId;           // Mã giao dịch VNPay (nếu có)
        private LocalDateTime createdAt;
    }
}