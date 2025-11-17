package com.example.shop_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.shop_backend.model.enums.PaymentMethod;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.shop_backend.model.enums.OrderStatus;
import com.example.shop_backend.model.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(precision = 15, scale = 0, nullable = false)
    private BigDecimal subtotal;

    @Column(precision = 15, scale = 0)
    private BigDecimal discountAmount;

    @Column(precision = 10, scale = 0, nullable = false)
    private BigDecimal shippingFeeOriginal; // phí ship gốc

    @Column(precision = 10, scale = 0)
    private BigDecimal shippingDiscount; // số tiền giảm

    @Column(precision = 10, scale = 0, nullable = false)
    private BigDecimal shippingFee; // phí ship thực tế

    @Column(precision = 15, scale = 0, nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    // Danh sách voucher áp dụng
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderVoucher> appliedVouchers = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}