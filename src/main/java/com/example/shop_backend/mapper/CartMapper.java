package com.example.shop_backend.mapper;

import com.example.shop_backend.dto.response.CartItemResponse;
import com.example.shop_backend.model.CartItem;
import org.mapstruct.*;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "productVariantId", source = "productVariant.id")
    @Mapping(target = "productName", source = "productVariant.product.name")
    @Mapping(target = "productSku", source = "productVariant.product.sku")
    @Mapping(target = "colorName", source = "productVariant.color.name")
    @Mapping(target = "sizeName", source = "productVariant.size.name")
    @Mapping(target = "price", source = "productVariant.product.price")
    @Mapping(target = "discountPrice", source = "productVariant.product.discountPrice")
    @Mapping(target = "imageUrl", expression = "java(getFirstImage(cartItem))")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    // Lấy ảnh đầu tiên của ProductVariant (nếu có)
    default String getFirstImage(CartItem cartItem) {
        return Optional.ofNullable(cartItem.getProductVariant().getProduct().getImages())
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0).getImageUrl())
                .orElse(null);
    }
}
