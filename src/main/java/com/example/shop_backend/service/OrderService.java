package com.example.shop_backend.service;

import com.example.shop_backend.dto.request.CreateOrderRequest;
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

        voucher.setUsageCount(voucher.getUsageCount() + 1);
        voucherRepository.save(voucher);

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
                .build();

        orderRepository.save(order);

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
        Order order = orderRepository.findByIdWithPayment(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        boolean isOwnerOrAdmin =
                user.getRole() == Role.ADMIN || user.getRole() == Role.OWNER;

        if (!isOwnerOrAdmin && !order.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Lấy danh sách variantId đã review bởi user
        List<Integer> reviewedVariantIds = reviewRepository.findReviewedVariantIdsByUserAndOrder(user.getId(), order.getId());

        // Map OrderItem -> OrderItemResponse và set reviewed flag
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
}
