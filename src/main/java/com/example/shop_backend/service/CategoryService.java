package com.example.shop_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.repository.ProductCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop_backend.dto.request.CategoryRequest;
import com.example.shop_backend.dto.response.CategoryResponse;
import com.example.shop_backend.mapper.CategoryMapper;
import com.example.shop_backend.model.Category;
import com.example.shop_backend.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductCategoryRepository productCategoryRepository;
    private final CloudinaryService cloudinaryService; // Thêm service upload ảnh

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> {
                    CategoryResponse response = categoryMapper.toResponse(category);
                    Long count = productCategoryRepository.countByCategoryId(category.getId());
                    response.setProductCount(count);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(Integer id) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        CategoryResponse response = categoryMapper.toResponse(category);
        response.setProductCount(productCategoryRepository.countByCategoryId(id));

        return response;
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTS);
        }

        // Map name + description
        Category category = categoryMapper.toEntity(request);

        // Upload ảnh lên Cloudinary và gán vào category
        var imageFile = request.getImage();
        String imageUrl = cloudinaryService.uploadImage(imageFile);
        category.setImage(imageUrl);

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Integer id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        // Kiểm tra name trùng với danh mục khác
        if (categoryRepository.existsByName(request.getName())
                && !category.getName().equals(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTS);
        }

        // Map lại name + description (image bỏ qua)
        categoryMapper.updateEntityFromRequest(request, category);

        // Nếu client gửi ảnh mới → upload và cập nhật
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(request.getImage());
            category.setImage(imageUrl);
        }

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        // Kiểm tra xem danh mục có đang liên kết với sản phẩm không
        boolean hasProducts = productCategoryRepository.existsByCategory_Id(id);
        if (hasProducts) {
            throw new AppException(ErrorCode.CATEGORY_HAS_PRODUCTS);
        }

        categoryRepository.delete(category);
    }
}
