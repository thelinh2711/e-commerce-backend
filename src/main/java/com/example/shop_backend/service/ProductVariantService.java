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
    
    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private com.example.shop_backend.mapper.ProductVariantMapper productVariantMapper;

    /**
     * Lấy variant theo ID với filter active
     * @param id - ID variant
     */
    public ProductVariantResponse getVariantById(Integer id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));

        return productVariantMapper.toResponse(variant);
    }

    /**
     * Lấy tất cả variants của product với filter active
     * @param productId - ID sản phẩm
     * @param active - null: lấy tất cả, true: chỉ active, false: chỉ inactive
     */
    public List<ProductVariantResponse> getVariantsByProductId(Integer productId, Boolean active) {
        productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        List<ProductVariant> variants;
        
        if (active == null) {
            // Lấy tất cả variants
            variants = productVariantRepository.findByProductIdWithColorAndSize(productId);
        } else {
            // Lọc theo active
            variants = productVariantRepository.findByProductIdAndActive(productId, active);
        }

        return variants.stream()
                .map(productVariantMapper::toResponse)
                .collect(Collectors.toList());
    }

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
                .active(true) // Mặc định active
                .build();

        ProductVariant savedVariant = productVariantRepository.save(variant);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (MultipartFile imageFile : request.getImages()) {
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                
                ProductVariantImage variantImage = ProductVariantImage.builder()
                        .productVariant(savedVariant)
                        .imageUrl(imageUrl)
                        .build();
                
                productVariantImageRepository.save(variantImage);
            }
        }

        return productVariantMapper.toResponse(savedVariant);
    }

    @Transactional
    public ProductVariantResponse updateVariant(Integer id, 
            com.example.shop_backend.dto.request.UpdateProductVariantRequest request) {
        
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));

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
        
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            productVariantImageRepository.deleteByProductVariantId(id);
            
            for (MultipartFile imageFile : request.getImages()) {
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                
                ProductVariantImage variantImage = ProductVariantImage.builder()
                        .productVariant(updatedVariant)
                        .imageUrl(imageUrl)
                        .build();
                
                productVariantImageRepository.save(variantImage);
            }
        }

        return productVariantMapper.toResponse(updatedVariant);
    }

    @Transactional
    public void deleteVariant(Integer id) {
        ProductVariant variant = productVariantRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));
        variant.setActive(false);
        ProductVariant saved = productVariantRepository.save(variant);
        productVariantRepository.flush();
    }
}