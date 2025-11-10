package com.example.shop_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.example.shop_backend.dto.request.CategoryRequest;
import com.example.shop_backend.dto.response.CategoryResponse;
import com.example.shop_backend.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Bỏ qua image, xử lý trong service
    @Mapping(target = "image", ignore = true)
    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);

    // Cập nhật cũng bỏ qua image
    @Mapping(target = "image", ignore = true)
    void updateEntityFromRequest(CategoryRequest request, @MappingTarget Category category);
}
