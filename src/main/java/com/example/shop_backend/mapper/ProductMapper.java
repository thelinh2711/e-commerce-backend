package com.example.shop_backend.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.shop_backend.dto.response.ProductResponse;
import com.example.shop_backend.model.Product;
import com.example.shop_backend.model.ProductVariant;
import com.example.shop_backend.model.ProductVariantImage;
import com.example.shop_backend.repository.ProductImageRepository;
import com.example.shop_backend.repository.ProductVariantImageRepository;
import com.example.shop_backend.repository.ProductVariantRepository;

/**
 * ProductMapper - MapStruct mapper để convert Product entity sang ProductResponse DTO
 */
@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    @Autowired
    protected ProductImageRepository productImageRepository;
    
    @Autowired
    protected ProductVariantRepository productVariantRepository;
    
    @Autowired
    protected ProductVariantImageRepository productVariantImageRepository;

    // Base URL cho images
    protected static final String BASE_URL = "http://localhost:8080";
    // protected static final String BASE_URL = "https://fnzv9bcp-8080.asse.devtunnels.ms";

    /**
     * Convert Product entity sang ProductResponse DTO
     */
    @Mapping(target = "id", expression = "java(product.getId().toString())")
    @Mapping(target = "brand", expression = "java(product.getBrand() != null ? product.getBrand().getName() : \"KHÁC\")")
    @Mapping(target = "price", source = "product", qualifiedByName = "mapPriceInfo")
    @Mapping(target = "images", source = "product", qualifiedByName = "mapProductImages")
    @Mapping(target = "variants", source = "product", qualifiedByName = "mapVariants")
    @Mapping(target = "totalCount", source = "product", qualifiedByName = "calculateTotalCount")
    @Mapping(target = "sold", source = "product", qualifiedByName = "calculateSold")
    public abstract ProductResponse toProductResponse(Product product);

    /**
     * Map price information từ Product entity
     */
    @Named("mapPriceInfo")
    protected ProductResponse.PriceInfo mapPriceInfo(Product product) {
        return ProductResponse.PriceInfo.builder()
                .price(product.getPrice())
                .costPrice(product.getCostPrice())
                .currency("VND")
                .discountPercent(product.getDiscountPercent())
                .build();
    }

    /**
     * Map product images với base URL
     */
    @Named("mapProductImages")
    protected List<String> mapProductImages(Product product) {
        return productImageRepository.findByProductIdOrderByDisplayOrderAsc(product.getId())
                .stream()
                .map(img -> BASE_URL + "/" + img.getImageUrl())
                .collect(Collectors.toList());
    }

    /**
     * Map product variants với color, size, images
     */
    @Named("mapVariants")
    protected List<ProductResponse.VariantInfo> mapVariants(Product product) {
        List<ProductVariant> variants = productVariantRepository
                .findByProductIdWithColorAndSize(product.getId());
        
        List<ProductResponse.VariantInfo> variantInfos = new ArrayList<>();

        for (ProductVariant variant : variants) {
            List<ProductVariantImage> variantImages = productVariantImageRepository
                    .findByProductVariantId(variant.getId());
            String variantImageUrl = variantImages.isEmpty() 
                ? "" 
                : BASE_URL + "/" + variantImages.get(0).getImageUrl();

            variantInfos.add(ProductResponse.VariantInfo.builder()
                    .size(variant.getSize() != null ? variant.getSize().getName() : "")
                    .image(variantImageUrl)
                    .stock(variant.getStock())
                    .colorName(variant.getColor() != null ? variant.getColor().getName() : "")
                    .colorHex(variant.getColor() != null ? variant.getColor().getHexCode() : "")
                    .build());
        }

        return variantInfos;
    }

    /**
     * Calculate total_count (tổng stock hiện tại từ variants)
     */
    @Named("calculateTotalCount")
    protected Integer calculateTotalCount(Product product) {
        return productVariantRepository.findByProductIdWithColorAndSize(product.getId())
                .stream()
                .mapToInt(ProductVariant::getStock)
                .sum();
    }

    /**
     * Calculate sold (số lượng đã bán = total_product - total_count)
     */
    @Named("calculateSold")
    protected Integer calculateSold(Product product) {
        return product.getTotalProduct() - calculateTotalCount(product);
    }
}
