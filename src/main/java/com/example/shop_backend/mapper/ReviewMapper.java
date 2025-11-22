package com.example.shop_backend.mapper;

import com.example.shop_backend.dto.response.ReviewResponse;
import com.example.shop_backend.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productVariantId", source = "productVariant.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "color", source = "productVariant.color.name")
    @Mapping(target = "size", source = "productVariant.size.name")
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "createdAt", source = "createdAt")
    ReviewResponse toReviewResponse(Review review);
}
