package com.example.shop_backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop_backend.dto.request.CreateProductRequest;
import com.example.shop_backend.dto.request.UpdateProductRequest;
import com.example.shop_backend.dto.response.ProductResponse;
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
import com.example.shop_backend.model.ProductVariantImage;
import com.example.shop_backend.repository.BrandRepository;
import com.example.shop_backend.repository.CategoryRepository;
import com.example.shop_backend.repository.LabelRepository;
import com.example.shop_backend.repository.ProductCategoryRepository;
import com.example.shop_backend.repository.ProductImageRepository;
import com.example.shop_backend.repository.ProductLabelRepository;
import com.example.shop_backend.repository.ProductRepository;
import com.example.shop_backend.repository.ProductVariantImageRepository;
import com.example.shop_backend.repository.ProductVariantRepository;

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

    /**
     * Lấy danh sách tất cả sản phẩm
     * 
     * @return List<ProductResponse> - Danh sách sản phẩm đã được convert sang DTO
     * 
     * Logic:
     * 1. Lấy tất cả products từ database
     * 2. Convert mỗi product entity sang ProductResponse DTO bằng ProductMapper
     * 3. Trả về danh sách đã convert
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        
        return products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết 1 sản phẩm theo ID
     * 
     * @param id - ID của sản phẩm cần lấy
     * @return ProductResponse - Thông tin chi tiết sản phẩm
     * @throws AppException nếu không tìm thấy sản phẩm
     * 
     * Logic:
     * 1. Tìm product theo ID trong database
     * 2. Nếu không tìm thấy -> throw exception PRODUCT_NOT_FOUND
     * 3. Convert product entity sang ProductResponse DTO bằng ProductMapper
     * 4. Trả về response
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        
        return productMapper.toProductResponse(product);
    }

    /**
     * Tạo sản phẩm mới
     * 
     * @param request - Dữ liệu sản phẩm mới (CreateProductRequest)
     * @return ProductResponse - Thông tin sản phẩm vừa tạo
     * @throws AppException nếu brand/category/label/color/size không tồn tại
     * 
     * Logic:
     * 1. Validate và lấy brand từ database
     * 2. Tạo product entity với thông tin cơ bản:
     *    - name, description, price: từ request
     *    - costPrice: set NULL (không có trong request)
     *    - discountPercent: từ request hoặc mặc định 0
     *    - discountPrice: tính = price * (1 - discountPercent/100)
     * 3. Lưu product vào database -> nhận ID tự động
     * 4. Thêm categories: Duyệt categoryIds và tạo ProductCategory
     * 5. Thêm labels: Duyệt labelIds và tạo ProductLabel
     * 6. Thêm images: Duyệt images và tạo ProductImage
     *    - displayOrder bắt đầu từ 1
     *    - Ảnh đầu tiên tự động là thumbnail
     * 7. Trả về ProductResponse của sản phẩm vừa tạo
     */
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        // Get brand
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        // Calculate discount price
        BigDecimal discountPrice = request.getPrice();
        if (request.getDiscountPercent() != null && request.getDiscountPercent() > 0) {
            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                new BigDecimal(request.getDiscountPercent()).divide(new BigDecimal(100))
            );
            discountPrice = request.getPrice().multiply(discountMultiplier)
                .setScale(0, java.math.RoundingMode.HALF_UP);
        }

        // Create product
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .costPrice(null) // Cost price is null if not provided
                .discountPercent(request.getDiscountPercent() != null ? request.getDiscountPercent() : 0)
                .discountPrice(discountPrice)
                .brand(brand)
                .build();

        product = productRepository.save(product);
        
        // Auto-generate SKU after product is saved (format: SP{id})
        product.setSku("SP" + product.getId());
        product = productRepository.save(product);

        // Add categories
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

        // Add labels
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

        // Add images
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (CreateProductRequest.ProductImageRequest imageReq : request.getImages()) {
                ProductImage productImage = ProductImage.builder()
                        .product(product)
                        .imageUrl(imageReq.getImageUrl())
                        .altText(imageReq.getAltText())
                        .build();
                
                productImageRepository.save(productImage);
            }
        }

        // Return created product
        Product savedProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        
        return productMapper.toProductResponse(savedProduct);
    }

    /**
     * Cập nhật thông tin sản phẩm
     * 
     * @param id - ID của sản phẩm cần update
     * @param request - Dữ liệu cập nhật (UpdateProductRequest)
     * @return ProductResponse - Thông tin sản phẩm sau khi update
     * @throws AppException nếu product/brand/category/label không tồn tại
     * 
     * Logic:
     * 1. Tìm product hiện tại theo ID
     * 2. Validate và lấy brand mới
     * 3. Cập nhật thông tin cơ bản:
     *    - name, description, price: từ request
     *    - costPrice: set NULL
     *    - discountPercent: từ request hoặc 0
     *    - discountPrice: tính lại = price * (1 - discountPercent/100)
     * 4. Cập nhật categories: Xóa cũ và thêm mới
     * 5. Cập nhật labels: Xóa cũ và thêm mới
     * 6. Cập nhật images: Xóa cũ và thêm mới
     * 7. Trả về ProductResponse đã update
     */
    @Transactional
    public ProductResponse updateProduct(Integer id, UpdateProductRequest request) {
        // Get existing product
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Update name if provided
        if (request.getName() != null) {
            product.setName(request.getName());
        }

        // Update description if provided
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        // Update brand if provided
        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
            product.setBrand(brand);
        }

        // Update price and recalculate discount price if price or discountPercent changed
        boolean priceChanged = request.getPrice() != null;
        boolean discountPercentChanged = request.getDiscountPercent() != null;

        if (priceChanged) {
            product.setPrice(request.getPrice());
        }

        if (discountPercentChanged) {
            product.setDiscountPercent(request.getDiscountPercent());
        }

        // Recalculate discount price if price or discount percent changed
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

        // Update categories: Xóa cũ và thêm mới
        if (request.getCategoryIds() != null) {
            // Xóa tất cả categories cũ
            List<ProductCategory> oldCategories = productCategoryRepository.findByProductId(id);
            if (!oldCategories.isEmpty()) {
                productCategoryRepository.deleteAll(oldCategories);
                productCategoryRepository.flush(); // Force delete trước khi insert
            }

            // Thêm categories mới
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

        // Update labels: Xóa cũ và thêm mới
        if (request.getLabelIds() != null) {
            // Xóa tất cả labels cũ
            List<ProductLabel> oldLabels = productLabelRepository.findByProductId(id);
            if (!oldLabels.isEmpty()) {
                productLabelRepository.deleteAll(oldLabels);
                productLabelRepository.flush(); // Force delete trước khi insert
            }

            // Thêm labels mới
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

        // Update images: Xóa cũ và thêm mới
        if (request.getImages() != null) {
            // Xóa tất cả images cũ
            List<ProductImage> oldImages = productImageRepository.findByProductId(id);
            if (!oldImages.isEmpty()) {
                productImageRepository.deleteAll(oldImages);
                productImageRepository.flush(); // Force delete trước khi insert
            }

            // Thêm images mới
            for (UpdateProductRequest.ProductImageRequest imageReq : request.getImages()) {
                ProductImage productImage = ProductImage.builder()
                        .product(product)
                        .imageUrl(imageReq.getImageUrl())
                        .altText(imageReq.getAltText())
                        .build();
                
                productImageRepository.save(productImage);
            }
        }

        // Return updated product
        Product updatedProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        
        return productMapper.toProductResponse(updatedProduct);
    }

    /**
     * Xóa sản phẩm
     * 
     * @param id - ID của sản phẩm cần xóa
     * @throws AppException nếu không tìm thấy sản phẩm
     * 
     * Logic:
     * 1. Kiểm tra product có tồn tại không
     * 2. Xóa theo thứ tự từ bảng phụ thuộc đến bảng chính:
     *    a. Xóa variant images (của từng variant)
     *    b. Xóa variants
     *    c. Xóa product images
     *    d. Xóa product labels
     *    e. Xóa product categories
     *    f. Xóa product
     * 
     * Note: Phải xóa theo thứ tự này để tránh lỗi foreign key constraint
     */
    @Transactional
    public void deleteProduct(Integer id) {
        // Check if product exists
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Xóa theo thứ tự: variant images -> variants -> images -> labels -> categories -> product
        
        // 1. Xóa variant images và variants
        List<ProductVariant> variants = productVariantRepository.findByProductId(id);
        for (ProductVariant variant : variants) {
            List<ProductVariantImage> variantImages = productVariantImageRepository.findByProductVariantId(variant.getId());
            productVariantImageRepository.deleteAll(variantImages);
        }
        productVariantRepository.deleteAll(variants);

        // 2. Xóa product images
        List<ProductImage> images = productImageRepository.findByProductId(id);
        productImageRepository.deleteAll(images);

        // 3. Xóa product labels
        List<ProductLabel> labels = productLabelRepository.findByProductId(id);
        productLabelRepository.deleteAll(labels);

        // 4. Xóa product categories
        List<ProductCategory> categories = productCategoryRepository.findByProductId(id);
        productCategoryRepository.deleteAll(categories);

        // 5. Xóa product
        productRepository.delete(product);
    }
}