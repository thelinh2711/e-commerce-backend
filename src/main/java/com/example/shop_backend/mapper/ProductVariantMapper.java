package com.example.shop_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.example.shop_backend.dto.response.ProductVariantResponse;
import com.example.shop_backend.model.ProductVariant;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {
    ProductVariantMapper INSTANCE = Mappers.getMapper(ProductVariantMapper.class);

    @Mapping(target = "color", expression = "java(mapColorInfo(variant))")
    @Mapping(target = "size", expression = "java(mapSizeInfo(variant))")
    @Mapping(target = "images", expression = "java(mapImages(variant))")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    ProductVariantResponse toResponse(ProductVariant variant);

    @Named("mapColorInfo")
    default ProductVariantResponse.ColorInfo mapColorInfo(ProductVariant variant) {
        if (variant.getColor() == null) return null;
        return ProductVariantResponse.ColorInfo.builder()
                .id(variant.getColor().getId())
                .name(variant.getColor().getName())
                .hexCode(variant.getColor().getHexCode())
                .build();
    }

    @Named("mapSizeInfo")
    default ProductVariantResponse.SizeInfo mapSizeInfo(ProductVariant variant) {
        if (variant.getSize() == null) return null;
        return ProductVariantResponse.SizeInfo.builder()
                .id(variant.getSize().getId())
                .name(variant.getSize().getName())
                .build();
    }

    @Named("mapImages")
    default List<String> mapImages(ProductVariant variant) {
        if (variant.getImages() == null) return List.of();
        return variant.getImages().stream()
                .map(img -> img.getImageUrl())
                .collect(Collectors.toList());
    }
}
