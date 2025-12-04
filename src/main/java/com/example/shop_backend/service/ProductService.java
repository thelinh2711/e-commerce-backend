package com.example.shop_backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.shop_backend.dto.request.CreateProductRequest;
import com.example.shop_backend.dto.request.UpdateProductRequest;
import com.example.shop_backend.dto.response.ProductResponse;
import com.example.shop_backend.dto.response.ProductSearchResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.mapper.ProductMapper;
import com.example.shop_backend.model.Brand;
import com.example.shop_backend.model.Category;
import com.example.shop_backend.model.Label;
import com.example.shop_backend.model.Product;
import com.example.shop_backend.model.ProductCategory;
import com.example.shop_backend.model.ProductImage;
import com.example.shop_backend.model.ProductLabel;
import com.example.shop_backend.model.ProductVariant;
import com.example.shop_backend.model.User;
import com.example.shop_backend.model.enums.ProductSex;
import com.example.shop_backend.model.enums.Role;
import com.example.shop_backend.repository.BrandRepository;
import com.example.shop_backend.repository.CategoryRepository;
import com.example.shop_backend.repository.LabelRepository;
import com.example.shop_backend.repository.ProductCategoryRepository;
import com.example.shop_backend.repository.ProductImageRepository;
import com.example.shop_backend.repository.ProductLabelRepository;
import com.example.shop_backend.repository.ProductRepository;
import com.example.shop_backend.repository.ProductVariantImageRepository;
import com.example.shop_backend.repository.ProductVariantRepository;
import com.example.shop_backend.specification.ProductSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductLabelRepository productLabelRepository;
    private final ProductVariantImageRepository productVariantImageRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final LabelRepository labelRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;

    /**
     * Lấy tất cả sản phẩm hoặc filter theo active
     * @param active - null: lấy tất cả, true: chỉ active, false: chỉ inactive
     * @param startId - null: lấy tất cả, có giá trị: lấy N sản phẩm từ id này
     * @param size - số lượng sản phẩm cần lấy (mặc định 12)
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(Boolean active, Integer startId, Integer size, User currentUser) {
        List<Product> products;
        
        if (startId != null) {
            // Lấy N sản phẩm từ startId (size tùy chỉnh)
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, size);
            
            if (active == null) {
                products = productRepository.findByIdGreaterThanEqualWithBrand(startId, pageable);
            } else {
                products = productRepository.findByIdGreaterThanEqualAndActiveWithBrand(startId, active, pageable);
            }
        } else {
            // Lấy tất cả sản phẩm
            if (active == null) {
                products = productRepository.findAllWithBrand();
            } else {
                products = productRepository.findByActiveWithBrand(active);
            }
        }
        
        if (products.isEmpty()) {
            return List.of();
        }
        
        // Batch load tất cả data liên quan để tránh N+1 queries
        List<Integer> productIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        
        // Pre-load tất cả images, variants, categories, labels
        var imagesMap = productImageRepository.findByProductIdIn(productIds).stream()
                .collect(Collectors.groupingBy(img -> img.getProduct().getId()));
        
        var variantsMap = productVariantRepository.findByProductIdInWithColorAndSize(productIds).stream()
                .collect(Collectors.groupingBy(v -> v.getProduct().getId()));
        
        var categoriesMap = productCategoryRepository.findByProductIdInWithCategory(productIds).stream()
                .collect(Collectors.groupingBy(pc -> pc.getProduct().getId()));
        
        var labelsMap = productLabelRepository.findByProductIdInWithLabel(productIds).stream()
                .collect(Collectors.groupingBy(pl -> pl.getProduct().getId()));
        
        // Pre-load variant images nếu cần
        List<Integer> variantIds = variantsMap.values().stream()
                .flatMap(List::stream)
                .map(ProductVariant::getId)
                .collect(Collectors.toList());
        
        var variantImagesMap = variantIds.isEmpty() ? java.util.Map.<Integer, List<com.example.shop_backend.model.ProductVariantImage>>of() :
                productVariantImageRepository.findByProductVariantIdIn(variantIds).stream()
                .collect(Collectors.groupingBy(vi -> vi.getProductVariant().getId()));
        
        boolean isOwner = currentUser != null && currentUser.getRole() == Role.OWNER;
        
        return products.stream()
                .map(product -> productMapper.toProductResponseWithPreloadedData(
                        product,
                        imagesMap.getOrDefault(product.getId(), List.of()),
                        variantsMap.getOrDefault(product.getId(), List.of()),
                        categoriesMap.getOrDefault(product.getId(), List.of()),
                        labelsMap.getOrDefault(product.getId(), List.of()),
                        variantImagesMap,
                        isOwner
                ))
                .collect(Collectors.toList());
    }

    /**
     * Lấy sản phẩm theo ID (không phụ thuộc vào active)
     * @param id - ID sản phẩm
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Integer id, User currentUser) {
        Product product = productRepository.findByIdWithBrand(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        
        // Pre-load tất cả data liên quan để tránh N+1 queries
        var images = productImageRepository.findByProductId(id);
        var variants = productVariantRepository.findByProductIdWithColorAndSize(id);
        var categories = productCategoryRepository.findByProductIdWithCategory(id);
        var labels = productLabelRepository.findByProductIdWithLabel(id);
        
        // Pre-load variant images
        List<Integer> variantIds = variants.stream()
                .map(ProductVariant::getId)
                .collect(Collectors.toList());
        
        var variantImagesMap = variantIds.isEmpty() ? java.util.Map.<Integer, List<com.example.shop_backend.model.ProductVariantImage>>of() :
                productVariantImageRepository.findByProductVariantIdIn(variantIds).stream()
                .collect(Collectors.groupingBy(vi -> vi.getProductVariant().getId()));
        
        boolean isOwner = currentUser != null && currentUser.getRole() == Role.OWNER;
        
        return productMapper.toProductResponseWithPreloadedData(
                product,
                images,
                variants,
                categories,
                labels,
                variantImagesMap,
                isOwner
        );
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request, User currentUser) {
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        BigDecimal discountPrice = request.getPrice();
        if (request.getDiscountPercent() != null && request.getDiscountPercent() > 0) {
            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                new BigDecimal(request.getDiscountPercent()).divide(new BigDecimal(100))
            );
            discountPrice = request.getPrice().multiply(discountMultiplier)
                .setScale(0, java.math.RoundingMode.HALF_UP);
        }

        Product product = Product.builder()
                .name(request.getName())
                .sex(request.getSex())
                .description(request.getDescription())
                .price(request.getPrice())
                .costPrice(null)
                .discountPercent(request.getDiscountPercent() != null ? request.getDiscountPercent() : 0)
                .discountPrice(discountPrice)
                .brand(brand)
                .active(true) // Mặc định là active
                .build();

        if (currentUser != null && currentUser.getRole() == Role.OWNER && request.getCostPrice() != null) {
            product.setCostPrice(request.getCostPrice());
        }

        product = productRepository.save(product);
        product.setSku("SP" + product.getId());
        product = productRepository.save(product);

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            for (Integer categoryId : request.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
                
                ProductCategory productCategory = ProductCategory.builder()
                        .product(product)
                        .category(category)
                        .build();
                
                productCategoryRepository.save(productCategory);
            }
        }

        if (request.getLabelIds() != null && !request.getLabelIds().isEmpty()) {
            for (Integer labelId : request.getLabelIds()) {
                Label label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new AppException(ErrorCode.LABEL_NOT_FOUND));
                
                ProductLabel productLabel = ProductLabel.builder()
                        .product(product)
                        .label(label)
                        .build();
                
                productLabelRepository.save(productLabel);
            }
        }

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<String> altTexts = request.getImageAltTexts();
            
            for (int i = 0; i < request.getImages().size(); i++) {
                MultipartFile imageFile = request.getImages().get(i);
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                String altText = (altTexts != null && i < altTexts.size()) 
                    ? altTexts.get(i) 
                    : null;
                
                ProductImage productImage = ProductImage.builder()
                        .product(product)
                        .imageUrl(imageUrl)
                        .altText(altText)
                        .build();
                
                productImageRepository.save(productImage);
            }
        }

        Product savedProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        
        if (currentUser != null && currentUser.getRole() == Role.OWNER) {
            return productMapper.toProductResponseForOwner(savedProduct);
        }
        
        return productMapper.toProductResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Integer id, UpdateProductRequest request, User currentUser) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (request.getCostPrice() != null && currentUser.getRole() != Role.OWNER) {
            throw new AppException(ErrorCode.FORBIDDEN, "Bạn không có quyền cập nhật giá vốn sản phẩm");
        }

        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getSex() != null) {
            product.setSex(request.getSex());
        }

        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
            product.setBrand(brand);
        }

        if (request.getCostPrice() != null && currentUser.getRole() == Role.OWNER) {
            product.setCostPrice(request.getCostPrice());
        }

        boolean priceChanged = request.getPrice() != null;
        boolean discountPercentChanged = request.getDiscountPercent() != null;

        if (priceChanged) {
            product.setPrice(request.getPrice());
        }

        if (discountPercentChanged) {
            product.setDiscountPercent(request.getDiscountPercent());
        }

        if (priceChanged || discountPercentChanged) {
            BigDecimal currentPrice = product.getPrice();
            Integer currentDiscountPercent = product.getDiscountPercent();
            
            BigDecimal discountPrice = currentPrice;
            if (currentDiscountPercent != null && currentDiscountPercent > 0) {
                BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                    new BigDecimal(currentDiscountPercent).divide(new BigDecimal(100))
                );
                discountPrice = currentPrice.multiply(discountMultiplier)
                    .setScale(0, java.math.RoundingMode.HALF_UP);
            }
            product.setDiscountPrice(discountPrice);
        }

        product = productRepository.save(product);

        if (request.getCategoryIds() != null) {
            List<ProductCategory> oldCategories = productCategoryRepository.findByProductId(id);
            if (!oldCategories.isEmpty()) {
                productCategoryRepository.deleteAll(oldCategories);
                productCategoryRepository.flush();
            }

            for (Integer categoryId : request.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
                
                ProductCategory productCategory = ProductCategory.builder()
                        .product(product)
                        .category(category)
                        .build();
                
                productCategoryRepository.save(productCategory);
            }
        }

        if (request.getLabelIds() != null) {
            List<ProductLabel> oldLabels = productLabelRepository.findByProductId(id);
            if (!oldLabels.isEmpty()) {
                productLabelRepository.deleteAll(oldLabels);
                productLabelRepository.flush();
            }

            for (Integer labelId : request.getLabelIds()) {
                Label label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new AppException(ErrorCode.LABEL_NOT_FOUND));
                
                ProductLabel productLabel = ProductLabel.builder()
                        .product(product)
                        .label(label)
                        .build();
                
                productLabelRepository.save(productLabel);
            }
        }

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<ProductImage> oldImages = productImageRepository.findByProductId(id);
            List<Integer> keepIds = request.getKeepImageIds();
            
            if (!oldImages.isEmpty()) {
                for (ProductImage oldImage : oldImages) {
                    if (keepIds == null || !keepIds.contains(oldImage.getId())) {
                        productImageRepository.delete(oldImage);
                    }
                }
                productImageRepository.flush();
            }
            
            List<String> altTexts = request.getImageAltTexts();
            for (int i = 0; i < request.getImages().size(); i++) {
                MultipartFile imageFile = request.getImages().get(i);
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                
                String altText = (altTexts != null && i < altTexts.size()) 
                    ? altTexts.get(i) 
                    : null;
                
                ProductImage productImage = ProductImage.builder()
                        .product(product)
                        .imageUrl(imageUrl)
                        .altText(altText)
                        .build();
                
                productImageRepository.save(productImage);
            }
        }

        Product updatedProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        
        if (currentUser.getRole() == Role.OWNER) {
            return productMapper.toProductResponseForOwner(updatedProduct);
        }
        
        return productMapper.toProductResponse(updatedProduct);
    }

    /**
     * Search và filter products với phân trang
     * @param search - từ khóa tìm kiếm (tên sản phẩm)
     * @param categories - danh sách category (OR)
     * @param sexList - danh sách giới tính (OR)
     * @param brands - danh sách brand (OR)
     * @param priceMin - giá tối thiểu
     * @param priceMax - giá tối đa
     * @param sort - kiểu sắp xếp (default, name-desc, price-asc, price-desc)
     * @param page - trang hiện tại (1-based)
     * @param size - số item mỗi trang
     * @param currentUser - user hiện tại
     * @return ProductSearchResponse với data, totalPages, totalElements
     */
    @Transactional(readOnly = true)
    public ProductSearchResponse searchProducts(
            String search,
            List<String> categories,
            List<String> sexList,
            List<String> brands,
            BigDecimal priceMin,
            BigDecimal priceMax,
            String sort,
            Integer page,
            Integer size,
            User currentUser) {
        
        // Convert sex strings to ProductSex enum
        List<ProductSex> productSexList = null;
        if (sexList != null && !sexList.isEmpty()) {
            productSexList = new ArrayList<>();
            for (String sex : sexList) {
                try {
                    productSexList.add(ProductSex.valueOf(sex.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    // Bỏ qua giá trị không hợp lệ
                }
            }
        }
        
        // Xác định sort order
        Sort sortOrder;
        if (sort != null) {
            switch (sort.toLowerCase()) {
                case "name-desc":
                    sortOrder = Sort.by(Sort.Direction.DESC, "name");
                    break;
                case "price-asc":
                    sortOrder = Sort.by(Sort.Direction.ASC, "discountPrice");
                    break;
                case "price-desc":
                    sortOrder = Sort.by(Sort.Direction.DESC, "discountPrice");
                    break;
                default: // "default" hoặc không truyền
                    sortOrder = Sort.by(Sort.Direction.ASC, "name");
                    break;
            }
        } else {
            sortOrder = Sort.by(Sort.Direction.ASC, "name");
        }
        
        // Tạo Pageable (Spring Data JPA dùng 0-based, nhưng FE truyền 1-based)
        int pageIndex = (page != null && page > 0) ? page - 1 : 0;
        int pageSize = (size != null && size > 0) ? size : 12;
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sortOrder);
        
        // Tạo Specification với tất cả filters
        Specification<Product> spec = ProductSpecification.filterProducts(
            search,
            categories,
            productSexList,
            brands,
            priceMin,
            priceMax,
            true // chỉ lấy sản phẩm active
        );
        
        // Query database với Specification và Pageable
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        List<Product> products = productPage.getContent();
        
        // Nếu không có sản phẩm, trả về empty
        if (products.isEmpty()) {
            return ProductSearchResponse.builder()
                    .success(true)
                    .data(List.of())
                    .totalPages(0)
                    .totalElements(0L)
                    .currentPage(page != null ? page : 1)
                    .pageSize(pageSize)
                    .build();
        }
        
        // Batch load tất cả related data
        List<Integer> productIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        
        // Load images
        List<ProductImage> allImages = productImageRepository.findByProductIdIn(productIds);
        Map<Integer, List<ProductImage>> imagesMap = allImages.stream()
                .collect(Collectors.groupingBy(img -> img.getProduct().getId()));
        
        // Load variants
        List<ProductVariant> allVariants = productVariantRepository.findByProductIdInWithColorAndSize(productIds);
        Map<Integer, List<ProductVariant>> variantsMap = allVariants.stream()
                .collect(Collectors.groupingBy(v -> v.getProduct().getId()));
        
        // Load categories
        List<ProductCategory> allCategories = productCategoryRepository.findByProductIdInWithCategory(productIds);
        Map<Integer, List<ProductCategory>> categoriesMap = allCategories.stream()
                .collect(Collectors.groupingBy(pc -> pc.getProduct().getId()));
        
        // Load labels
        List<ProductLabel> allLabels = productLabelRepository.findByProductIdInWithLabel(productIds);
        Map<Integer, List<ProductLabel>> labelsMap = allLabels.stream()
                .collect(Collectors.groupingBy(pl -> pl.getProduct().getId()));
        
        // Load variant images
        List<Integer> variantIds = allVariants.stream()
                .map(ProductVariant::getId)
                .collect(Collectors.toList());
        
        List<com.example.shop_backend.model.ProductVariantImage> allVariantImages = 
                variantIds.isEmpty() ? List.of() : productVariantImageRepository.findByProductVariantIdIn(variantIds);
        
        Map<Integer, List<com.example.shop_backend.model.ProductVariantImage>> variantImagesMap = 
                allVariantImages.stream()
                        .collect(Collectors.groupingBy(vi -> vi.getProductVariant().getId()));
        
        // Convert to ProductResponse
        boolean isOwner = currentUser != null && currentUser.getRole() == Role.OWNER;
        List<ProductResponse> productResponses = products.stream()
                .map(product -> productMapper.toProductResponseWithPreloadedData(
                        product,
                        imagesMap.getOrDefault(product.getId(), List.of()),
                        variantsMap.getOrDefault(product.getId(), List.of()),
                        categoriesMap.getOrDefault(product.getId(), List.of()),
                        labelsMap.getOrDefault(product.getId(), List.of()),
                        variantImagesMap,
                        isOwner
                ))
                .collect(Collectors.toList());
        
        return ProductSearchResponse.builder()
                .success(true)
                .data(productResponses)
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .currentPage(page != null ? page : 1)
                .pageSize(pageSize)
                .build();
    }

    @Transactional
    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        product.setActive(false);
        productRepository.save(product);

        List<ProductVariant> variants = productVariantRepository.findByProductId(id);
        for (ProductVariant variant : variants) {
            variant.setActive(false);
            productVariantRepository.save(variant);
        }
    }
}