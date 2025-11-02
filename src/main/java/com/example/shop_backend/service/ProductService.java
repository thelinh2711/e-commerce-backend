package com.example.shop_backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop_backend.dto.response.ProductResponse;
import com.example.shop_backend.model.Product;
import com.example.shop_backend.model.ProductImage;
import com.example.shop_backend.model.ProductLabel;
import com.example.shop_backend.model.ProductVariant;
import com.example.shop_backend.model.ProductVariantImage;
import com.example.shop_backend.repository.ProductImageRepository;
import com.example.shop_backend.repository.ProductLabelRepository;
import com.example.shop_backend.repository.ProductRepository;
import com.example.shop_backend.repository.ProductVariantImageRepository;
import com.example.shop_backend.repository.ProductVariantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductLabelRepository productLabelRepository;
    private final ProductVariantImageRepository productVariantImageRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAllActiveProducts();
        
        return products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse convertToProductResponse(Product product) {
        // Get product images
        List<ProductImage> productImages = productImageRepository.findByProductIdOrderByDisplayOrderAsc(product.getId());
        List<String> imageUrls = productImages.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        // Get product variants with color and size
        List<ProductVariant> variants = productVariantRepository.findByProductIdWithColorAndSize(product.getId());
        List<ProductResponse.VariantInfo> variantInfos = new ArrayList<>();
        int totalStock = 0;

        for (ProductVariant variant : variants) {
            // Get variant image
            List<ProductVariantImage> variantImages = productVariantImageRepository.findByProductVariantId(variant.getId());
            String variantImageUrl = variantImages.isEmpty() ? "" : variantImages.get(0).getImageUrl();

            ProductResponse.VariantInfo variantInfo = ProductResponse.VariantInfo.builder()
                    .colorName(variant.getColor() != null ? variant.getColor().getName() : "")
                    .colorHex(variant.getColor() != null ? variant.getColor().getHexCode() : "")
                    .size(variant.getSize() != null ? variant.getSize().getName() : "")
                    .image(variantImageUrl)
                    .stock(variant.getStock())
                    .build();

            variantInfos.add(variantInfo);
            totalStock += variant.getStock();
        }

        // Get product labels
        List<ProductLabel> productLabels = productLabelRepository.findByProductIdWithLabel(product.getId());
        List<String> labels = productLabels.stream()
                .map(pl -> pl.getLabel().getName())
                .collect(Collectors.toList());

        // Calculate price info
        BigDecimal currentPrice = product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice();
        BigDecimal originalPrice = product.getPrice();
        Integer discountPercent = product.getDiscountPercent();

        ProductResponse.PriceInfo priceInfo = ProductResponse.PriceInfo.builder()
                .current(currentPrice)
                .original(originalPrice)
                .discountPercent(discountPercent)
                .currency("VND")
                .build();

        // Check if product is new arrival (created within last 30 days)
        boolean isNewArrival = product.getCreatedAt() != null &&
                ChronoUnit.DAYS.between(product.getCreatedAt(), LocalDateTime.now()) <= 30;

        // Check if product is best seller (sold > 100 or high rating)
        boolean isBestSeller = product.getSold() > 100 || 
                (product.getRating() != null && product.getRating().compareTo(new BigDecimal("4.5")) >= 0);

        return ProductResponse.builder()
                .id(product.getId().toString())
                .name(product.getName())
                .slug(product.getSlug())
                .brand(product.getBrand() != null ? product.getBrand().getName() : "KHÁC")
                .price(priceInfo)
                .images(imageUrls)
                .variants(variantInfos)
                .totalCount(totalStock)
                .labels(labels)
                .isWishlisted(false) // TODO: Check if user has wishlisted this product
                .isBestSeller(isBestSeller)
                .isNewArrival(isNewArrival)
                .url("/product/" + product.getSlug())
                .build();
    }
}
