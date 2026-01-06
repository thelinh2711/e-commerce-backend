package com.example.shop_backend.service;

import com.example.shop_backend.dto.request.CreateOrderRequest;
import com.example.shop_backend.dto.response.OrderDashboardStatsResponse;
import com.example.shop_backend.dto.response.OrderListResponse;
import com.example.shop_backend.dto.response.OrderResponse;
import com.example.shop_backend.dto.response.PageResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.mapper.OrderMapper;
import com.example.shop_backend.model.*;
import com.example.shop_backend.model.enums.*;
import com.example.shop_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository variantRepository;
    private final VoucherRepository voucherRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderVoucherRepository orderVoucherRepository;

    private static final BigDecimal DEFAULT_SHIPPING_FEE = new BigDecimal("30000");

    // =====================
    // Ép tiền về không có .00
    // =====================
    private BigDecimal money(BigDecimal value) {
        if (value == null) return BigDecimal.ZERO;
        return value.setScale(0, RoundingMode.DOWN);
    }

    // =====================
    // Tính reward points
    // =====================
    private int calculateRewardPoints(BigDecimal totalAmount) {
        if (totalAmount.compareTo(new BigDecimal("1000000")) > 0) return 20000;
        if (totalAmount.compareTo(new BigDecimal("500000")) > 0) return 10000;
        if (totalAmount.compareTo(new BigDecimal("100000")) > 0) return 3000;
        return 1000;
    }

    // =====================
    // Voucher result helper
    // =====================
    private static class VoucherResult {
        private BigDecimal discountAmount = BigDecimal.ZERO;
        private BigDecimal shippingDiscount = BigDecimal.ZERO;
    }

    private VoucherResult applyVoucher(Voucher voucher, BigDecimal subtotal) {
        VoucherResult result = new VoucherResult();

        if (voucher.getStatus() != StatusVoucher.ACTIVE) {
            throw new AppException(ErrorCode.VOUCHER_NOT_ACTIVE);
        }

        if (voucher.getMinOrderValue() != null && subtotal.compareTo(voucher.getMinOrderValue()) < 0) {
            throw new AppException(ErrorCode.VOUCHER_MIN_ORDER_NOT_MET);
        }

        if (voucher.getUsageLimit() != null && voucher.getUsageCount() >= voucher.getUsageLimit()) {
            throw new AppException(ErrorCode.VOUCHER_OUT_OF_USES);
        }

        switch (voucher.getDiscountType()) {
            case FIXED_AMOUNT:
                result.discountAmount = voucher.getDiscountValue().min(subtotal);
                break;

            case PERCENTAGE:
                BigDecimal discount = subtotal.multiply(voucher.getDiscountValue())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                if (voucher.getMaxDiscountValue() != null &&
                        discount.compareTo(voucher.getMaxDiscountValue()) > 0)
                    discount = voucher.getMaxDiscountValue();

                result.discountAmount = discount;
                break;

            case FREESHIP:
                result.shippingDiscount = voucher.getDiscountValue() != null
                        ? voucher.getDiscountValue().min(DEFAULT_SHIPPING_FEE)
                        : DEFAULT_SHIPPING_FEE;
                break;
        }

        return result;
    }

    // =====================
    // Tạo order
    // =====================
    @Transactional
    public OrderResponse createOrder(User user, CreateOrderRequest request, List<Integer> cartItemIds) {

        if (request.getBuyNowItem() == null &&
                (cartItemIds == null || cartItemIds.isEmpty())) {
            throw new AppException(ErrorCode.INVALID_ORDER_REQUEST, "Không có sản phẩm để tạo đơn");
        }

        List<OrderItem> orderItems = new ArrayList<>();

        // BUY NOW
        if (request.getBuyNowItem() != null) {
            CreateOrderRequest.BuyNowItem buyNow = request.getBuyNowItem();
            ProductVariant variant = variantRepository.findById(buyNow.getProductVariantId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));

            if (variant.getStock() < buyNow.getQuantity()) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK,
                        String.format("Sản phẩm %s không đủ số lượng", variant.getProduct().getName()));
            }

            orderItems.add(orderMapper.buyNowItemToOrderItem(buyNow, variant));
        }

        // CART ITEMS
        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);

            if (cartItems.size() != cartItemIds.size()) {
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }

            for (CartItem cartItem : cartItems) {
                if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
                    throw new AppException(ErrorCode.UNAUTHORIZED);
                }

                if (cartItem.getProductVariant().getStock() < cartItem.getQuantity()) {
                    throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
                }

                orderItems.add(orderMapper.cartItemToOrderItem(cartItem));
            }
        }

        // SUBTOTAL
        BigDecimal subtotal = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingFeeOriginal = DEFAULT_SHIPPING_FEE;
        BigDecimal shippingDiscount = BigDecimal.ZERO;
        BigDecimal shippingFee = DEFAULT_SHIPPING_FEE;
        BigDecimal discountVoucherAmount = BigDecimal.ZERO;

        // APPLY VOUCHERS
        if (request.getVoucherCodes() != null) {
            if (request.getVoucherCodes().size() > 2)
                throw new AppException(ErrorCode.VOUCHER_INVALID_COMBINATION);

            List<Voucher> vouchers = request.getVoucherCodes().stream()
                    .map(code -> voucherRepository.findByCode(code)
                            .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND)))
                    .collect(Collectors.toList());

            long freeshipCount =
                    vouchers.stream().filter(v -> v.getDiscountType() == VoucherDiscountType.FREESHIP).count();

            long discountCount =
                    vouchers.stream().filter(v -> v.getDiscountType() != VoucherDiscountType.FREESHIP).count();

            if (freeshipCount > 1 || discountCount > 1)
                throw new AppException(ErrorCode.VOUCHER_INVALID_COMBINATION);

            for (Voucher voucher : vouchers) {
                VoucherResult result = applyVoucher(voucher, subtotal);
                discountVoucherAmount = discountVoucherAmount.add(result.discountAmount);
                shippingDiscount = shippingDiscount.add(result.shippingDiscount);
            }

            shippingFee = shippingFee.subtract(shippingDiscount);
            if (shippingFee.compareTo(BigDecimal.ZERO) < 0)
                shippingFee = BigDecimal.ZERO;
        }

        // REWARD POINTS
        int rewardUsed = request.getRewardPointsToUse() != null
                ? request.getRewardPointsToUse()
                : 0;

        if (rewardUsed > user.getRewardPoints()) {
            throw new AppException(ErrorCode.INVALID_REWARD_POINTS);
        }

        BigDecimal discountReward = new BigDecimal(rewardUsed);

        // TOTAL
        BigDecimal totalAmount = subtotal
                .subtract(discountVoucherAmount)
                .subtract(discountReward)
                .add(shippingFee);

        if (totalAmount.compareTo(BigDecimal.ZERO) < 0)
            totalAmount = BigDecimal.ZERO;

        // SAVE ORDER
        Order order = Order.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .note(request.getNote())
                .paymentMethod(request.getPaymentMethod())
                .status(OrderStatus.PENDING)
                .subtotal(money(subtotal))
                .discountAmount(money(discountVoucherAmount.add(discountReward)))
                .shippingFee(money(shippingFee))
                .shippingFeeOriginal(money(shippingFeeOriginal))
                .shippingDiscount(money(shippingDiscount))
                .totalAmount(money(totalAmount))
                .rewardPointsUsed(rewardUsed)
                .build();

        orderRepository.save(order);

        if (request.getVoucherCodes() != null && !request.getVoucherCodes().isEmpty()) {
            for (String code : request.getVoucherCodes()) {
                Voucher voucher = voucherRepository.findByCode(code)
                        .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

                // 1. Tăng usage count (CHỈ 1 LẦN)
                voucher.setUsageCount(voucher.getUsageCount() + 1);
                voucherRepository.save(voucher);

                // 2. Lưu OrderVoucher
                OrderVoucher orderVoucher = OrderVoucher.builder()
                        .order(order)   // ✅ order đã tồn tại
                        .voucher(voucher)
                        .build();

                orderVoucherRepository.save(orderVoucher);
            }
        }

        // SAVE PAYMENT
        // CHỈ TẠO PAYMENT CHO COD
        if (order.getPaymentMethod() == PaymentMethod.COD) {
            Payment payment = Payment.builder()
                    .order(order)
                    .amount(money(order.getTotalAmount()))
                    .paymentMethod(order.getPaymentMethod())
                    .status(PaymentStatus.UNPAID)
                    .transactionId(null)
                    .build();
            paymentRepository.save(payment);
            order.setPayment(payment);
        }

        // SAVE ITEMS
        for (OrderItem item : orderItems) {
            item.setOrder(order);
            orderItemRepository.save(item);

            ProductVariant variant = item.getProductVariant();
            variant.setStock(variant.getStock() - item.getQuantity());
            // variant.getProduct().setSold(variant.getProduct().getSold() + item.getQuantity());
        }

        // UPDATE REWARD POINTS
        user.setRewardPoints(user.getRewardPoints() - rewardUsed);
        int rewardEarned = calculateRewardPoints(totalAmount);
        user.setRewardPoints(user.getRewardPoints() + rewardEarned);
        userRepository.save(user);

        // DELETE CART ITEMS
        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            cartItemRepository.deleteAllById(cartItemIds);
        }

        // RESPONSE
        OrderResponse response = orderMapper.toOrderResponse(order);
        response.setRewardPointsUsed(rewardUsed);
        response.setRewardPointsEarned(rewardEarned);
        response.setUserRemainingRewardPoints(user.getRewardPoints());
        response.setItems(orderMapper.toOrderItemResponses(orderItems));

        return response;
    }

    @Transactional
    public OrderResponse updateOrderStatus(Integer orderId, OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Kiểm tra flow chuyển trạng thái
        validateTransition(order.getStatus(), newStatus);

        OrderStatus oldStatus = order.getStatus();

        // Cập nhật trạng thái đơn
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        // === Nếu giao thành công ===
        if (newStatus == OrderStatus.DELIVERED) {

            // 1. Cập nhật payment cho COD
            Payment payment = order.getPayment();
            if (payment != null
                    && payment.getPaymentMethod() == PaymentMethod.COD
                    && payment.getStatus() == PaymentStatus.UNPAID) {

                payment.setStatus(PaymentStatus.PAID);
                paymentRepository.save(payment);
            }

            // 2. Tăng số lượng đã bán (sold)
            for (OrderItem item : order.getItems()) {
                Product product = item.getProductVariant().getProduct();
                product.setSold(product.getSold() + item.getQuantity());
                productRepository.save(product);
            }
        }

        // === Nếu đơn bị hủy ===
        if (newStatus == OrderStatus.CANCELLED) {

            // Hoàn lại stock cho variant
            for (OrderItem item : order.getItems()) {
                ProductVariant variant = item.getProductVariant();
                variant.setStock(variant.getStock() + item.getQuantity());
                variantRepository.save(variant);
            }
        }

        return orderMapper.toOrderResponse(order);
    }


    /**
     * Search orders with optional keyword, status, fromDate, toDate
     */
    public PageResponse<OrderListResponse> searchOrders(
            String keyword,
            String statusStr,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    ) {
        // Parse status string -> enum
        OrderStatus status = null;
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                status = OrderStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AppException(
                        ErrorCode.INVALID_ORDER_STATUS,
                        "Invalid order status: " + statusStr
                );
            }
        }

        Page<OrderListResponse> page =
                orderRepository.searchOrderList(
                        keyword,
                        status,
                        fromDate,
                        toDate,
                        pageable
                );

        return PageResponse.<OrderListResponse>builder()
                .data(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }



    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(User user) {
        // ĐỔI từ findByUser → findByUserWithPayment
        List<Order> orders = orderRepository.findByUserWithPayment(user);

        // DEBUG - Xem payment có null không
        System.out.println("===== DEBUG ORDERS =====");
        for (Order order : orders) {
            System.out.println("Order ID: " + order.getId());
            System.out.println("Payment: " + (order.getPayment() != null ? order.getPayment().getId() : "NULL"));
        }

        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetail(User user, Integer orderId) {
        // Query 1: Lấy Order + items + variant (không có images)
        Order order = orderRepository.findByIdWithPayment(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        boolean isOwnerOrAdmin = user.getRole() == Role.ADMIN || user.getRole() == Role.OWNER;
        if (!isOwnerOrAdmin && !order.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Query 2: Lấy images cho các variants (nếu cần)
        List<Integer> variantIds = order.getItems().stream()
                .map(item -> item.getProductVariant().getId())
                .toList();

        if (!variantIds.isEmpty()) {
            orderRepository.findVariantsWithImages(variantIds); // Load images vào cache
        }

        // Query 3: Lấy reviewed variants
        List<Integer> reviewedVariantIds =
                reviewRepository.findReviewedVariantIdsByUserAndOrder(user.getId(), order.getId());

        // Map response
        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> {
                    OrderResponse.OrderItemResponse resp = orderMapper.toOrderItemResponse(item);
                    resp.setReviewed(reviewedVariantIds.contains(item.getProductVariant().getId()));
                    return resp;
                })
                .toList();

        OrderResponse response = orderMapper.toOrderResponse(order);
        response.setItems(itemResponses);

        return response;
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {

        switch (current) {
            case PENDING:
                if (next != OrderStatus.CONFIRMED && next != OrderStatus.CANCELLED)
                    throw new AppException(ErrorCode.INVALID_STATUS_TRANSITION);
                break;

            case CONFIRMED:
                if (next != OrderStatus.SHIPPED && next != OrderStatus.CANCELLED)
                    throw new AppException(ErrorCode.INVALID_STATUS_TRANSITION);
                break;

            case SHIPPED:
                if (next != OrderStatus.DELIVERED)
                    throw new AppException(ErrorCode.INVALID_STATUS_TRANSITION);
                break;

            default:
                throw new AppException(ErrorCode.INVALID_STATUS_TRANSITION);
        }
    }

    public OrderDashboardStatsResponse getStats(
            LocalDateTime from,
            LocalDateTime to
    ) {
        Object result = orderRepository.countOrderDashboardStats(from, to);

        Object[] row = (Object[]) result;

        return OrderDashboardStatsResponse.builder()
                .total(((Number) row[0]).longValue())
                .pending(row[1] == null ? 0 : ((Number) row[1]).longValue())
                .confirmed(row[2] == null ? 0 : ((Number) row[2]).longValue())
                .shipped(row[3] == null ? 0 : ((Number) row[3]).longValue())
                .delivered(row[4] == null ? 0 : ((Number) row[4]).longValue())
                .build();
    }

    /**
     * Hủy order và hoàn lại: stock, voucher usage count, reward points
     * Dùng khi thanh toán thất bại/hủy
     */
    @Transactional
    public void cancelOrderAndRefund(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Chỉ hủy nếu order đang ở trạng thái PENDING
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new AppException(
                    ErrorCode.INVALID_STATUS_TRANSITION,
                    "Cannot cancel order with status: " + order.getStatus()
            );
        }

        System.out.println("===== CANCELLING ORDER AND REFUNDING =====");
        System.out.println("Order ID: " + order.getId());

        // 1. Hoàn lại stock cho các sản phẩm
        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getProductVariant();
            variant.setStock(variant.getStock() + item.getQuantity());
            variantRepository.save(variant);
            System.out.println("Restored stock: " + item.getQuantity() + " for variant ID: " + variant.getId());
        }

        // 2. Hoàn lại reward points cho user (CHÍNH XÁC)
        User user = order.getUser();

        // Lấy reward points đã sử dụng khi tạo order
        int rewardUsed = order.getRewardPointsUsed() != null ? order.getRewardPointsUsed() : 0;

        // Tính reward points đã cộng khi tạo order
        int rewardEarned = calculateRewardPoints(order.getTotalAmount());

        // Hoàn lại chính xác:
        // - Cộng lại reward đã dùng: +rewardUsed
        // - Trừ đi reward đã cộng: -rewardEarned
        int finalRewardPoints = user.getRewardPoints() + rewardUsed - rewardEarned;

        // Đảm bảo không âm
        if (finalRewardPoints < 0) {
            finalRewardPoints = 0;
        }

        user.setRewardPoints(finalRewardPoints);
        userRepository.save(user);

        System.out.println("Reward points refund:");
        System.out.println("   - Restored used points: +" + rewardUsed);
        System.out.println("   - Deducted earned points: -" + rewardEarned);
        System.out.println("   - User final points: " + finalRewardPoints);

        // 3. Hoàn lại voucher usage count
        List<OrderVoucher> appliedVouchers = order.getAppliedVouchers();
        System.out.println("Found " + appliedVouchers.size() + " vouchers to restore");

        for (OrderVoucher orderVoucher : appliedVouchers) {
            try {
                // Lấy voucher ID từ OrderVoucher
                Voucher voucherRef = orderVoucher.getVoucher();
                if (voucherRef == null) {
                    System.out.println("Voucher reference is null, skip restore");
                    continue;
                }

                Integer voucherId = voucherRef.getId();
                if (voucherId == null) {
                    System.out.println(" Voucher ID is null, skip restore");
                    continue;
                }

                // Kiểm tra voucher có tồn tại trong DB không
                Optional<Voucher> voucherOpt = voucherRepository.findById(voucherId);

                if (!voucherOpt.isPresent()) {
                    System.out.println("Voucher ID " + voucherId + " not found in database, skip restore");
                    continue;
                }

                // Lấy voucher từ DB
                Voucher voucher = voucherOpt.get();

                // Hoàn lại usage count
                int oldUsageCount = voucher.getUsageCount();
                int newUsageCount = oldUsageCount - 1;
                if (newUsageCount < 0) {
                    newUsageCount = 0;
                }
                voucher.setUsageCount(newUsageCount);
                voucherRepository.save(voucher);

                System.out.println("✅ Restored voucher: " + voucher.getCode() +
                        " | UsageCount: " + oldUsageCount + " → " + newUsageCount +
                        " | Status: " + voucher.getStatus());
            } catch (Exception e) {
                System.err.println("⚠️ Error restoring voucher: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // 4. Cập nhật order status thành CANCELLED
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        System.out.println("Order cancelled and refunded successfully");
    }
}
