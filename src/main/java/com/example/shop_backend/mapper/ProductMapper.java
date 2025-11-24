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

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    @Autowired
    protected ProductImageRepository productImageRepository;
    
    @Autowired
    protected ProductVariantRepository productVariantRepository;
    
    @Autowired
    protected ProductVariantImageRepository productVariantImageRepository;
    
    @Autowired
    protected com.example.shop_backend.repository.ProductCategoryRepository productCategoryRepository;
    
    @Autowired
    protected com.example.shop_backend.repository.ProductLabelRepository productLabelRepository;

    /**
     * Convert Product entity sang ProductResponse DTO (cho PUBLIC/ADMIN)
     */
    @Mapping(target = "id", expression = "java(product.getId().toString())")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "sku", source = "sku")
    @Mapping(target = "brand", source = "product", qualifiedByName = "mapBrand")
    @Mapping(target = "price", source = "product", qualifiedByName = "mapPriceInfo")
    @Mapping(target = "images", source = "product", qualifiedByName = "mapProductImages")
    @Mapping(target = "variants", source = "product", qualifiedByName = "mapVariants")
    @Mapping(target = "categories", source = "product", qualifiedByName = "mapCategories")
    @Mapping(target = "labels", source = "product", qualifiedByName = "mapLabels")
    @Mapping(target = "totalCount", source = "product", qualifiedByName = "calculateTotalCount")
    @Mapping(target = "sold", source = "product", qualifiedByName = "calculateSold")
    @org.mapstruct.Mapping(target = "active", source = "active")
    public abstract ProductResponse toProductResponse(Product product);

    /**
     * Convert Product entity sang ProductResponse DTO (cho OWNER - bao gồm costPrice)
     */
    @Mapping(target = "id", expression = "java(product.getId().toString())")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "sku", source = "sku")
    @Mapping(target = "brand", source = "product", qualifiedByName = "mapBrand")
    @Mapping(target = "price", source = "product", qualifiedByName = "mapPriceInfoForOwner")
    @Mapping(target = "images", source = "product", qualifiedByName = "mapProductImages")
    @Mapping(target = "variants", source = "product", qualifiedByName = "mapVariants")
    @Mapping(target = "categories", source = "product", qualifiedByName = "mapCategories")
    @Mapping(target = "labels", source = "product", qualifiedByName = "mapLabels")
    @Mapping(target = "totalCount", source = "product", qualifiedByName = "calculateTotalCount")
    @Mapping(target = "sold", source = "product", qualifiedByName = "calculateSold")
    @org.mapstruct.Mapping(target = "active", source = "active")
    public abstract ProductResponse toProductResponseForOwner(Product product);

    /**
     * Map price information (không bao gồm costPrice)
     */
    @Named("mapPriceInfo")
    protected ProductResponse.PriceInfo mapPriceInfo(Product product) {
        java.math.BigDecimal price = product.getPrice();
        Integer discountPercent = product.getDiscountPercent() != null ? product.getDiscountPercent() : 0;
        
        Long discountPrice = null;
        if (product.getDiscountPrice() != null) {
            discountPrice = product.getDiscountPrice().longValue();
        } else {
            discountPrice = price.longValue();
        }
        
        return ProductResponse.PriceInfo.builder()
                .price(price)
                .costPrice(null) // Không trả về costPrice
                .currency("VND")
                .discountPercent(discountPercent)
                .discountPrice(discountPrice)
                .build();
    }

    /**
     * Map price information cho OWNER (bao gồm costPrice)
     */
    @Named("mapPriceInfoForOwner")
    protected ProductResponse.PriceInfo mapPriceInfoForOwner(Product product) {
        java.math.BigDecimal price = product.getPrice();
        Integer discountPercent = product.getDiscountPercent() != null ? product.getDiscountPercent() : 0;
        
        Long discountPrice = null;
        if (product.getDiscountPrice() != null) {
            discountPrice = product.getDiscountPrice().longValue();
        } else {
            discountPrice = price.longValue();
        }
        
        return ProductResponse.PriceInfo.builder()
                .price(price)
                .costPrice(product.getCostPrice()) // ✅ Trả về costPrice cho OWNER
                .currency("VND")
                .discountPercent(discountPercent)
                .discountPrice(discountPrice)
                .build();
    }

    @Named("mapBrand")
    protected ProductResponse.BrandInfo mapBrand(Product product) {
        if (product.getBrand() == null) {
            return ProductResponse.BrandInfo.builder()
                    .id(null)
                    .name("KHÁC")
                    .build();
        }
        return ProductResponse.BrandInfo.builder()
                .id(product.getBrand().getId())
                .name(product.getBrand().getName())
                .build();
    }

    @Named("mapProductImages")
    protected List<ProductResponse.ImageInfo> mapProductImages(Product product) {
        return productImageRepository.findByProductId(product.getId())
            .stream()
            .map(img -> ProductResponse.ImageInfo.builder()
                .id(img.getId())
                .imageUrl(img.getImageUrl())
                .altText(img.getAltText())
                .build())
            .collect(Collectors.toList());
    }

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
            : variantImages.get(0).getImageUrl();

            variantInfos.add(ProductResponse.VariantInfo.builder()
                .id(variant.getId())
                .size(variant.getSize() != null ? variant.getSize().getName() : "")
                .image(variantImageUrl)
                .stock(variant.getStock())
                .colorName(variant.getColor() != null ? variant.getColor().getName() : "")
                .colorHex(variant.getColor() != null ? variant.getColor().getHexCode() : "")
                .active(variant.getActive())
                .build());
        }

        return variantInfos;
    }

    @Named("calculateTotalCount")
    protected Integer calculateTotalCount(Product product) {
        return productVariantRepository.findByProductIdWithColorAndSize(product.getId())
                .stream()
                .mapToInt(ProductVariant::getStock)
                .sum();
    }

    @Named("calculateSold")
    protected Integer calculateSold(Product product) {
        return product.getSold() != null ? product.getSold() : 0;
    }

    @Named("mapCategories")
    protected List<ProductResponse.CategoryInfo> mapCategories(Product product) {
        return productCategoryRepository.findByProductId(product.getId())
                .stream()
                .map(pc -> ProductResponse.CategoryInfo.builder()
                        .id(pc.getCategory().getId())
                        .name(pc.getCategory().getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Named("mapLabels")
    protected List<ProductResponse.LabelInfo> mapLabels(Product product) {
        return productLabelRepository.findByProductId(product.getId())
                .stream()
                .map(pl -> ProductResponse.LabelInfo.builder()
                        .id(pl.getLabel().getId())
                        .name(pl.getLabel().getName())
                        .build())
                .collect(Collectors.toList());
    }
}