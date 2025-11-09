package com.example.shop_backend.service;

import com.example.shop_backend.dto.request.BrandRequest;
import com.example.shop_backend.dto.response.BrandResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.mapper.BrandMapper;
import com.example.shop_backend.model.Brand;
import com.example.shop_backend.repository.BrandRepository;
import com.example.shop_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final BrandMapper brandMapper;
    private final CloudinaryService cloudinaryService;

    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(brandMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BrandResponse createBrand(BrandRequest request) {
        if (brandRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.BRAND_EXISTED);
        }

        Brand brand = brandMapper.toEntity(request);

        MultipartFile logoFile = request.getLogo();
        if (logoFile != null && !logoFile.isEmpty()) {
            String logoUrl = cloudinaryService.uploadImage(logoFile);
            brand.setLogo(logoUrl);
        }

        Brand saved = brandRepository.save(brand);
        return brandMapper.toResponse(saved);
    }

    @Transactional
    public BrandResponse updateBrand(Integer id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        brandMapper.updateEntity(brand, request);

        MultipartFile logoFile = request.getLogo();
        if (logoFile != null && !logoFile.isEmpty()) {
            String logoUrl = cloudinaryService.uploadImage(logoFile);
            brand.setLogo(logoUrl);
        }

        Brand updated = brandRepository.save(brand);
        return brandMapper.toResponse(updated);
    }

    @Transactional
    public void deleteBrand(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        boolean hasProducts = productRepository.existsByBrand(brand);
        if (hasProducts) {
            throw new AppException(ErrorCode.BRAND_HAS_PRODUCTS);
        }

        brandRepository.delete(brand);
    }

    @Transactional(readOnly = true)
    public BrandResponse getBrandById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        return brandMapper.toResponse(brand);
    }
}
