package com.example.shop_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.shop_backend.dto.response.WishlistResponse;
import com.example.shop_backend.model.Wishlist;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "sku", source = "product.sku")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "brand", source = "product.brand.name")
    @Mapping(target = "images", expression = "java(mapProductImages(wishlist))")
    @Mapping(target = "sold", source = "product.sold")
    @Mapping(target = "createdAt", source = "product.createdAt")
    @Mapping(target = "likeCount", ignore = true) // sẽ set thủ công ở service
    @Mapping(target = "price", expression = "java(mapPriceInfo(wishlist))")
    WishlistResponse toWishlistResponse(Wishlist wishlist);

    // ✅ Map product images
    default java.util.List<String> mapProductImages(Wishlist wishlist) {
        if (wishlist.getProduct().getImages() != null) {
            return wishlist.getProduct().getImages().stream()
                    .map(img -> img.getImageUrl())
                    .collect(java.util.stream.Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }

    // ✅ Map thông tin giá
    default WishlistResponse.PriceInfo mapPriceInfo(Wishlist wishlist) {
        var product = wishlist.getProduct();
        return WishlistResponse.PriceInfo.builder()
                .discountPrice(product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice())
                .price(product.getPrice())
                .discountPercent(product.getDiscountPercent())
                .currency("VND")
                .build();
    }
}
