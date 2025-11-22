package com.example.shop_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.shop_backend.dto.request.CreateProductVariantRequest;
import com.example.shop_backend.dto.response.ProductVariantResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.model.*;
import com.example.shop_backend.repository.*;

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
    
    // ✅ THÊM: CloudinaryService
    @Autowired
    private CloudinaryService cloudinaryService;

    public ProductVariantResponse getVariantById(Integer id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        return convertToResponse(variant);
    }

    public List<ProductVariantResponse> getVariantsByProductId(Integer productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        List<ProductVariant> variants = productVariantRepository.findByProductIdWithColorAndSize(productId);

        return variants.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * ✅ THAY ĐỔI: Upload ảnh từ file
     */
    @Transactional
    public ProductVariantResponse createVariant(CreateProductVariantRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        Color color = null;
        if (request.getColorId() != null) {
            color = colorRepository.findById(request.getColorId())
                    .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND));
        }

        Size size = null;
        if (request.getSizeId() != null) {
            size = sizeRepository.findById(request.getSizeId())
                    .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_FOUND));
        }

        // Check duplicate
        java.util.Optional<ProductVariant> existing = productVariantRepository.findDuplicate(
                request.getProductId(),
                color != null ? color.getId() : null,
                size != null ? size.getId() : null
        );
        if (existing.isPresent()) {
            throw new AppException(ErrorCode.PRODUCT_VARIANT_EXISTED);
        }

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .color(color)
                .size(size)
                .stock(request.getStock() != null ? request.getStock() : 0)
                .build();

        ProductVariant savedVariant = productVariantRepository.save(variant);

        // ✅ THAY ĐỔI: Upload ảnh từ file
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (MultipartFile imageFile : request.getImages()) {
                // Upload lên Cloudinary
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                
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
     * ✅ THAY ĐỔI: Upload ảnh từ file
     */
    @Transactional
    public ProductVariantResponse updateVariant(Integer id, 
            com.example.shop_backend.dto.request.UpdateProductVariantRequest request) {
        
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        boolean hasChanges = false;
        
        Color newColor = variant.getColor();
        if (request.getColorId() != null) {
            newColor = colorRepository.findById(request.getColorId())
                    .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND));
            hasChanges = true;
        }
        
        Size newSize = variant.getSize();
        if (request.getSizeId() != null) {
            newSize = sizeRepository.findById(request.getSizeId())
                    .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_FOUND));
            hasChanges = true;
        }
        
        // Check duplicate
        if (hasChanges) {
            java.util.Optional<ProductVariant> existing = productVariantRepository.findDuplicate(
                    variant.getProduct().getId(),
                    newColor != null ? newColor.getId() : null,
                    newSize != null ? newSize.getId() : null
            );
            
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new AppException(ErrorCode.PRODUCT_VARIANT_EXISTED);
            }
            
            variant.setColor(newColor);
            variant.setSize(newSize);
        }
        
        if (request.getStock() != null) {
            int newStock = variant.getStock() + request.getStock();
            variant.setStock(newStock);
        }
        
        ProductVariant updatedVariant = productVariantRepository.save(variant);
        
        // ✅ THAY ĐỔI: Upload ảnh mới từ file
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            // Xóa ảnh cũ
            productVariantImageRepository.deleteByProductVariantId(id);
            
            // Upload ảnh mới
            for (MultipartFile imageFile : request.getImages()) {
                // Upload lên Cloudinary
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                
                ProductVariantImage variantImage = ProductVariantImage.builder()
                        .productVariant(updatedVariant)
                        .imageUrl(imageUrl)
                        .build();
                
                productVariantImageRepository.save(variantImage);
            }
        }

        return convertToResponse(updatedVariant);
    }

    @Transactional
    public void deleteVariant(Integer id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        productVariantImageRepository.deleteByProductVariantId(id);
        productVariantRepository.delete(variant);
    }

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

        List<String> images = productVariantImageRepository.findByProductVariantId(variant.getId())
                .stream()
                .map(img -> img.getImageUrl())
                .collect(Collectors.toList());

        return ProductVariantResponse.builder()
                .id(variant.getId())
                .productId(variant.getProduct().getId())
                .productName(variant.getProduct().getName())
                .stock(variant.getStock())
                .color(colorInfo)
                .size(sizeInfo)
                .images(images)
                .createdAt(variant.getCreatedAt())
                .build();
    }
}