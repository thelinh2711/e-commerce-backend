package com.example.shop_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop_backend.dto.request.CreateProductVariantRequest;
import com.example.shop_backend.dto.response.ProductVariantResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.model.Color;
import com.example.shop_backend.model.Product;
import com.example.shop_backend.model.ProductVariant;
import com.example.shop_backend.model.ProductVariantImage;
import com.example.shop_backend.model.Size;
import com.example.shop_backend.repository.ColorRepository;
import com.example.shop_backend.repository.ProductRepository;
import com.example.shop_backend.repository.ProductVariantImageRepository;
import com.example.shop_backend.repository.ProductVariantRepository;
import com.example.shop_backend.repository.SizeRepository;

/**
 * Service for managing Product Variants
 * Handles business logic for variant operations including creation and retrieval
 */
@Service
public class ProductVariantService {

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ProductVariantImageRepository productVariantImageRepository;

    /**
     * Get variant by ID
     * 
     * @param id Variant ID
     * @return ProductVariantResponse containing variant details
     * @throws AppException if variant not found
     */
    public ProductVariantResponse getVariantById(Integer id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        return convertToResponse(variant);
    }

    /**
     * Get all variants of a specific product
     * 
     * @param productId Product ID
     * @return List of ProductVariantResponse
     * @throws AppException if product not found
     */
    public List<ProductVariantResponse> getVariantsByProductId(Integer productId) {
        // Verify product exists
        productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        List<ProductVariant> variants = productVariantRepository.findByProductIdWithColorAndSize(productId);

        return variants.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create a new product variant
     * Auto-generates SKU in format: VAR-{productId}-{counter}
     * 
     * @param request CreateProductVariantRequest containing variant data
     * @return ProductVariantResponse of created variant
     * @throws AppException if product/color/size not found
     */
    @Transactional
    public ProductVariantResponse createVariant(CreateProductVariantRequest request) {
        // Validate product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // Validate color if provided
        Color color = null;
        if (request.getColorId() != null) {
            color = colorRepository.findById(request.getColorId())
                    .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND));
        }

        // Validate size if provided
        Size size = null;
        if (request.getSizeId() != null) {
            size = sizeRepository.findById(request.getSizeId())
                    .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_FOUND));
        }

                // Check duplicate variant (same product + color + size)
                java.util.Optional<ProductVariant> existing = productVariantRepository.findDuplicate(
                                request.getProductId(),
                                color != null ? color.getId() : null,
                                size != null ? size.getId() : null
                );
                if (existing.isPresent()) {
                        throw new AppException(ErrorCode.PRODUCT_VARIANT_EXISTED);
                }

        // Generate SKU
        int variantCount = productVariantRepository.findByProductId(request.getProductId()).size();
        String sku = "VAR-" + request.getProductId() + "-" + (variantCount + 1);

        // Create variant
        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .color(color)
                .size(size)
                .sku(sku)
                .stock(request.getStock() != null ? request.getStock() : 0)
                .price(request.getPrice())
                .build();

        ProductVariant savedVariant = productVariantRepository.save(variant);

        // Update product's total_product (add new variant stock)
        updateProductTotalStock(product.getId());

        // Add variant images if provided
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (String imageUrl : request.getImages()) {
                ProductVariantImage variantImage = ProductVariantImage.builder()
                        .productVariant(savedVariant)
                        .imageUrl(imageUrl)
                        .build();
                
                productVariantImageRepository.save(variantImage);
            }
        }

        return convertToResponse(savedVariant);
    }

    /**
     * Update a product variant's stock
     * Recalculates product's total_product after update
     * 
     * @param id Variant ID
     * @param newStock New stock value
     * @return ProductVariantResponse of updated variant
     * @throws AppException if variant not found
     */
    @Transactional
    public ProductVariantResponse updateVariantStock(Integer id, Integer newStock) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        variant.setStock(newStock);
        ProductVariant updatedVariant = productVariantRepository.save(variant);

        // Update product's total_product
        updateProductTotalStock(variant.getProduct().getId());

        return convertToResponse(updatedVariant);
    }

    /**
     * Delete a product variant
     * Recalculates product's total_product after deletion
     * 
     * @param id Variant ID
     * @throws AppException if variant not found
     */
    @Transactional
    public void deleteVariant(Integer id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        Integer productId = variant.getProduct().getId();
        
        // Delete variant images first
        productVariantImageRepository.deleteByProductVariantId(id);
        
        // Delete variant
        productVariantRepository.delete(variant);

        // Update product's total_product
        updateProductTotalStock(productId);
    }

    /**
     * Recalculate and update product's total_product based on all variants
     * total_product = sum of all variant stocks
     * 
     * @param productId Product ID
     */
    private void updateProductTotalStock(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // Calculate total stock from all variants
        Integer totalStock = productVariantRepository.findByProductId(productId)
                .stream()
                .mapToInt(ProductVariant::getStock)
                .sum();

        product.setTotalProduct(totalStock);
        productRepository.save(product);
    }

    /**
     * Convert ProductVariant entity to ProductVariantResponse DTO
     * 
     * @param variant ProductVariant entity
     * @return ProductVariantResponse DTO
     */
    private ProductVariantResponse convertToResponse(ProductVariant variant) {
        ProductVariantResponse.ColorInfo colorInfo = null;
        if (variant.getColor() != null) {
            colorInfo = ProductVariantResponse.ColorInfo.builder()
                    .id(variant.getColor().getId())
                    .name(variant.getColor().getName())
                    .hexCode(variant.getColor().getHexCode())
                    .build();
        }

        ProductVariantResponse.SizeInfo sizeInfo = null;
        if (variant.getSize() != null) {
            sizeInfo = ProductVariantResponse.SizeInfo.builder()
                    .id(variant.getSize().getId())
                    .name(variant.getSize().getName())
                    .build();
        }

        // Get variant images with base URL
        List<String> images = productVariantImageRepository.findByProductVariantId(variant.getId())
                .stream()
                .map(img -> "http://localhost:8080/" + img.getImageUrl())
                .collect(Collectors.toList());

        return ProductVariantResponse.builder()
                .id(variant.getId())
                .productId(variant.getProduct().getId())
                .productName(variant.getProduct().getName())
                .sku(variant.getSku())
                .stock(variant.getStock())
                .price(variant.getPrice())
                .color(colorInfo)
                .size(sizeInfo)
                .images(images)
                .createdAt(variant.getCreatedAt())
                .build();
    }
}
