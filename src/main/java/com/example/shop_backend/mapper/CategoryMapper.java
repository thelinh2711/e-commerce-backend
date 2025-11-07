package com.example.shop_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import com.example.shop_backend.dto.request.CategoryRequest;
import com.example.shop_backend.dto.response.CategoryResponse;
import com.example.shop_backend.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryRequest request);
    CategoryResponse toResponse(Category category);

    void updateEntityFromRequest(CategoryRequest request, @MappingTarget Category category);
}
