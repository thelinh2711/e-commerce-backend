package com.example.shop_backend.service;

import com.example.shop_backend.dto.request.RevenueStatisticsRequest;
import com.example.shop_backend.dto.response.RevenueStatisticsResponse;
import com.example.shop_backend.dto.response.RevenueStatisticsResponse.*;
import com.example.shop_backend.model.*;
import com.example.shop_backend.model.enums.PaymentStatus;
import com.example.shop_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevenueStatisticsService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * Lấy danh sách payments đã thanh toán theo khoảng thời gian
     * Hỗ trợ lọc theo paymentId và giới hạn số lượng
     */
    private List<Payment> getFilteredPayments(RevenueStatisticsRequest request) {
        LocalDateTime fromDate = request.getFromDate();
        LocalDateTime toDate = request.getToDate();
        Integer paymentId = request.getPaymentId();
        Integer limit = request.getLimit() != null ? request.getLimit() : 30;
        
        List<Payment> allPaidPayments = paymentRepository.findByStatus(PaymentStatus.PAID);
        
        // Lọc theo thời gian
        List<Payment> filteredByDate = allPaidPayments.stream()
                .filter(p -> {
                    if (fromDate != null && p.getCreatedAt().isBefore(fromDate)) {
                        return false;
                    }
                    if (toDate != null && p.getCreatedAt().isAfter(toDate)) {
                        return false;
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Payment::getCreatedAt).reversed())
                .collect(Collectors.toList());
        
        // Nếu có paymentId, lọc từ payment đó trở đi
        if (paymentId != null) {
            int index = -1;
            for (int i = 0; i < filteredByDate.size(); i++) {
                if (filteredByDate.get(i).getId().equals(paymentId)) {
                    index = i;
                    break;
                }
            }
            
            if (index >= 0) {
                // Lấy từ vị trí đó trở đi, tối đa 'limit' bản ghi
                int endIndex = Math.min(index + limit, filteredByDate.size());
                return filteredByDate.subList(index, endIndex);
            }
        }
        
        // Nếu không có paymentId, lấy 'limit' bản ghi đầu tiên
        return filteredByDate.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    // =====================================================
    // DOANH THU - Cho ADMIN và OWNER
    // =====================================================

    /**
     * Lấy thống kê CHỈ doanh thu với orderDetails
     */
    @Transactional(readOnly = true)
    public RevenueOnlyStats getRevenueOnly(RevenueStatisticsRequest request) {
        List<Payment> payments = getFilteredPayments(request);
        
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalProducts = 0;
        List<OrderRevenueDetail> orderDetails = new ArrayList<>();
        
        for (Payment payment : payments) {
            totalRevenue = totalRevenue.add(payment.getAmount());
            Order order = payment.getOrder();
            
            List<ProductRevenueDetail> productDetails = new ArrayList<>();
            
            for (OrderItem item : order.getItems()) {
                totalProducts += item.getQuantity();
                
                ProductVariant variant = item.getProductVariant();
                
                ProductVariantInfo variantInfo = ProductVariantInfo.builder()
                        .variantId(variant.getId())
                        .quantity(item.getQuantity())
                        .color(variant.getColor() != null ? variant.getColor().getName() : null)
                        .size(variant.getSize() != null ? variant.getSize().getName() : null)
                        .build();
                
                productDetails.add(ProductRevenueDetail.builder()
                        .productId(variant.getProduct().getId())
                        .productName(variant.getProduct().getName())
                        .sku(variant.getProduct().getSku())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice().setScale(0, RoundingMode.DOWN))
                        .revenue(item.getTotalPrice().setScale(0, RoundingMode.DOWN))
                        .productVariant(variantInfo)
                        .costPrice(null) // ADMIN không thấy
                        .cost(null)
                        .profit(null)
                        .build());
            }
            
            orderDetails.add(OrderRevenueDetail.builder()
                    .orderId(order.getId())
                    .orderDate(order.getCreatedAt())
                    .orderRevenue(payment.getAmount().setScale(0, RoundingMode.DOWN))
                    .customerName(order.getUser().getFullName())
                    .customerEmail(order.getUser().getEmail())
                    .orderStatus(order.getStatus())
                    .paymentMethod(order.getPaymentMethod())
                    .products(productDetails)
                    .orderCost(null) // ADMIN không thấy
                    .orderProfit(null)
                    .build());
        }
        
        return RevenueOnlyStats.builder()
                .totalRevenue(totalRevenue.setScale(0, RoundingMode.DOWN))
                .totalOrders(payments.size())
                .totalProducts(totalProducts)
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .orderDetails(orderDetails)
                .build();
    }


    // =====================================================
    // LỢI NHUẬN - CHỈ OWNER có thể xem
    // =====================================================

    @Transactional(readOnly = true)
    public RevenueStatisticsResponse getRevenueStatistics(RevenueStatisticsRequest request) {
        List<Payment> payments = getFilteredPayments(request);
        
        OverviewStats overview = calculateOverviewStats(payments);
        List<OrderRevenueDetail> orderDetails = calculateOrderDetails(payments);
        
        return RevenueStatisticsResponse.builder()
                .overview(overview)
                .orderDetails(orderDetails)
                .build();
    }

    private OverviewStats calculateOverviewStats(List<Payment> payments) {
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        int totalProducts = 0;
        
        for (Payment payment : payments) {
            Order order = payment.getOrder();
            totalRevenue = totalRevenue.add(payment.getAmount());
            
            for (OrderItem item : order.getItems()) {
                Product product = item.getProductVariant().getProduct();
                
                BigDecimal costPrice = product.getCostPrice() != null 
                    ? product.getCostPrice() 
                    : item.getUnitPrice();
                
                BigDecimal itemCost = costPrice.multiply(new BigDecimal(item.getQuantity()));
                totalCost = totalCost.add(itemCost);
                totalProducts += item.getQuantity();
            }
        }
        
        BigDecimal totalProfit = totalRevenue.subtract(totalCost);
        BigDecimal profitMargin = BigDecimal.ZERO;
        
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            profitMargin = totalProfit
                    .divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        return OverviewStats.builder()
                .totalRevenue(totalRevenue.setScale(0, RoundingMode.DOWN))
                .totalCost(totalCost.setScale(0, RoundingMode.DOWN))
                .totalProfit(totalProfit.setScale(0, RoundingMode.DOWN))
                .profitMargin(profitMargin)
                .totalOrders(payments.size())
                .totalProducts(totalProducts)
                .build();
    }

    private List<OrderRevenueDetail> calculateOrderDetails(List<Payment> payments) {
        List<OrderRevenueDetail> details = new ArrayList<>();
        
        for (Payment payment : payments) {
            Order order = payment.getOrder();
            BigDecimal orderCost = BigDecimal.ZERO;
            List<ProductRevenueDetail> productDetails = new ArrayList<>();
            
            for (OrderItem item : order.getItems()) {
                Product product = item.getProductVariant().getProduct();
                ProductVariant variant = item.getProductVariant();
                
                BigDecimal costPrice = product.getCostPrice() != null 
                    ? product.getCostPrice() 
                    : item.getUnitPrice();
                
                BigDecimal itemRevenue = item.getTotalPrice();
                BigDecimal itemCost = costPrice.multiply(new BigDecimal(item.getQuantity()));
                BigDecimal itemProfit = itemRevenue.subtract(itemCost);
                
                orderCost = orderCost.add(itemCost);
                
                ProductVariantInfo variantInfo = ProductVariantInfo.builder()
                        .variantId(variant.getId())
                        .quantity(item.getQuantity())
                        .color(variant.getColor() != null ? variant.getColor().getName() : null)
                        .size(variant.getSize() != null ? variant.getSize().getName() : null)
                        .build();
                
                productDetails.add(ProductRevenueDetail.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .sku(product.getSku())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice().setScale(0, RoundingMode.DOWN))
                        .costPrice(costPrice.setScale(0, RoundingMode.DOWN))
                        .revenue(itemRevenue.setScale(0, RoundingMode.DOWN))
                        .cost(itemCost.setScale(0, RoundingMode.DOWN))
                        .profit(itemProfit.setScale(0, RoundingMode.DOWN))
                        .productVariant(variantInfo)
                        .build());
            }
            
            BigDecimal orderRevenue = payment.getAmount();
            BigDecimal orderProfit = orderRevenue.subtract(orderCost);
            
            details.add(OrderRevenueDetail.builder()
                    .orderId(order.getId())
                    .orderDate(order.getCreatedAt())
                    .orderRevenue(orderRevenue.setScale(0, RoundingMode.DOWN))
                    .orderCost(orderCost.setScale(0, RoundingMode.DOWN))
                    .orderProfit(orderProfit.setScale(0, RoundingMode.DOWN))
                    .customerName(order.getUser().getFullName())
                    .customerEmail(order.getUser().getEmail())
                    .orderStatus(order.getStatus())
                    .paymentMethod(order.getPaymentMethod())
                    .products(productDetails)
                    .build());
        }
        
        return details;
    }

    private BigDecimal calculateOrderProfit(Order order) {
        BigDecimal orderCost = BigDecimal.ZERO;
        
        for (OrderItem item : order.getItems()) {
            Product product = item.getProductVariant().getProduct();
            BigDecimal costPrice = product.getCostPrice() != null 
                ? product.getCostPrice() 
                : item.getUnitPrice();
            
            orderCost = orderCost.add(
                costPrice.multiply(new BigDecimal(item.getQuantity()))
            );
        }
        
        return order.getTotalAmount().subtract(orderCost);
    }
}