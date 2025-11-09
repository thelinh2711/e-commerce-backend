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
    
    @Autowired
    protected com.example.shop_backend.repository.ProductCategoryRepository productCategoryRepository;
    
    @Autowired
    protected com.example.shop_backend.repository.ProductLabelRepository productLabelRepository;

    /**
     * Convert Product entity sang ProductResponse DTO
     */
    @Mapping(target = "id", expression = "java(product.getId().toString())")
    @Mapping(target = "brand", source = "product", qualifiedByName = "mapBrand")
    @Mapping(target = "price", source = "product", qualifiedByName = "mapPriceInfo")
    @Mapping(target = "images", source = "product", qualifiedByName = "mapProductImages")
    @Mapping(target = "variants", source = "product", qualifiedByName = "mapVariants")
    @Mapping(target = "categories", source = "product", qualifiedByName = "mapCategories")
    @Mapping(target = "labels", source = "product", qualifiedByName = "mapLabels")
    @Mapping(target = "totalCount", source = "product", qualifiedByName = "calculateTotalCount")
    @Mapping(target = "sold", source = "product", qualifiedByName = "calculateSold")
    public abstract ProductResponse toProductResponse(Product product);

    /**
     * Map price information từ Product entity
     */
    @Named("mapPriceInfo")
    protected ProductResponse.PriceInfo mapPriceInfo(Product product) {
        java.math.BigDecimal price = product.getPrice();
        Integer discountPercent = product.getDiscountPercent() != null ? product.getDiscountPercent() : 0;
        java.math.BigDecimal discountPrice = product.getDiscountPrice() != null 
            ? product.getDiscountPrice() 
            : price;
        
        return ProductResponse.PriceInfo.builder()
                .price(price)
                .currency("VND")
                .discountPercent(discountPercent)
                .discountPrice(discountPrice)
                .build();
    }

    /**
     * Map brand information từ Product entity
     */
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

    /**
     * Map product images - trả về danh sách URL ảnh
     */
    @Named("mapProductImages")
    protected List<String> mapProductImages(Product product) {
        return productImageRepository.findByProductId(product.getId())
                .stream()
                .map(img -> img.getImageUrl())
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
                : variantImages.get(0).getImageUrl();

            variantInfos.add(ProductResponse.VariantInfo.builder()
                    .id(variant.getId())
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
     * Calculate sold (số lượng đã bán)
     * Lưu ý: Cần có logic riêng để tracking sold từ Order
     */
    @Named("calculateSold")
    protected Integer calculateSold(Product product) {
        // TODO: Implement logic tính sold từ Order/OrderItem
        // Hiện tại trả về giá trị mặc định từ product.sold
        return product.getSold() != null ? product.getSold() : 0;
    }

    /**
     * Map categories từ ProductCategory
     */
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

    /**
     * Map labels từ ProductLabel
     */
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
