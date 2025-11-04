package com.example.shop_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.shop_backend.model.enums.ProductStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2, name = "cost_price")
    private BigDecimal costPrice;

    @Column(precision = 10, scale = 2, name = "discount_price")
    private BigDecimal discountPrice;

    @Builder.Default
    @Column(name = "discount_percent", columnDefinition = "INT DEFAULT 0")
    private Integer discountPercent = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(unique = true, length = 100)
    private String sku;

    @Builder.Default
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer stock = 0;

    @Builder.Default
    @Column(name = "total_product", columnDefinition = "INT DEFAULT 0")
    private Integer totalProduct = 0;

    @Builder.Default
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer sold = 0;

    @Builder.Default
    @Column(precision = 3, scale = 2, columnDefinition = "DECIMAL(3,2) DEFAULT 0")
    private BigDecimal rating = BigDecimal.ZERO;

    @Builder.Default
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer view = 0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ACTIVE', 'INACTIVE', 'OUT_OF_STOCK') DEFAULT 'ACTIVE'")
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(unique = true)
    private String slug;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductLabel> productLabels = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
