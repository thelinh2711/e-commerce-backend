package com.example.shop_backend.mapper;

import com.example.shop_backend.dto.response.ReviewResponse;
import com.example.shop_backend.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mapping(target = "productId", source = "review.product.id")
    @Mapping(target = "productVariantId", source = "review.productVariant.id")
    @Mapping(target = "productName", source = "review.product.name")
    @Mapping(target = "color", source = "review.productVariant.color.name")
    @Mapping(target = "size", source = "review.productVariant.size.name")
    @Mapping(target = "orderId", source = "review.order.id")
    @Mapping(target = "id", source = "review.id")
    @Mapping(target = "rating", source = "review.rating")
    @Mapping(target = "title", source = "review.title")
    @Mapping(target = "comment", source = "review.comment")
    @Mapping(target = "createdAt", source = "review.createdAt")
    ReviewResponse toReviewResponse(Review review);
}

