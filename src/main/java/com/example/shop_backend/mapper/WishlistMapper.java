package com.example.shop_backend.mapper;

import com.example.shop_backend.dto.response.WishlistResponse;
import com.example.shop_backend.model.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "slug", source = "product.slug")
    @Mapping(target = "brand", source = "product.brand.name")
    @Mapping(
            target = "images",
            expression = "java(wishlist.getProduct().getImages() != null ? wishlist.getProduct().getImages().stream().map(img -> img.getImageUrl()).collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList())"
    )
    @Mapping(target = "totalProduct", source = "product.totalProduct")
    @Mapping(target = "totalCount", source = "product.totalProduct")
    @Mapping(target = "createdAt", source = "product.createdAt")
    @Mapping(target = "likeCount", ignore = true) // sẽ set thủ công ở service
    @Mapping(target = "price", expression = "java(mapPriceInfo(wishlist))")
    WishlistResponse toWishlistResponse(Wishlist wishlist);

    // ✅ Map thông tin giá
    default WishlistResponse.PriceInfo mapPriceInfo(Wishlist wishlist) {
        var product = wishlist.getProduct();
        return WishlistResponse.PriceInfo.builder()
                .current(product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice())
                .original(product.getPrice())
                .discountPercent(product.getDiscountPercent())
                .currency("VND")
                .build();
    }
}
