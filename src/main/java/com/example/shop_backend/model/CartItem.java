package com.example.shop_backend.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CartItems")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Mỗi CartItem thuộc về 1 Cart
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // Mỗi CartItem tương ứng với 1 ProductVariant
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @Column(nullable = false)
    private Integer quantity;

    // Tổng tiền = (discountPrice != null ? discountPrice : price) * quantity
//    @Column(name = "item_total_price", precision = 10, scale = 2)
//    private BigDecimal itemTotalPrice;
}
