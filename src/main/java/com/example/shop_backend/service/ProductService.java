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
import com.example.shop_backend.model.Color;
import com.example.shop_backend.model.Label;
import com.example.shop_backend.model.Product;
import com.example.shop_backend.model.ProductCategory;
import com.example.shop_backend.model.ProductImage;
import com.example.shop_backend.model.ProductLabel;
import com.example.shop_backend.model.ProductVariant;
import com.example.shop_backend.model.ProductVariantImage;
import com.example.shop_backend.model.Size;
import com.example.shop_backend.repository.BrandRepository;
import com.example.shop_backend.repository.CategoryRepository;
import com.example.shop_backend.repository.ColorRepository;
import com.example.shop_backend.repository.LabelRepository;
import com.example.shop_backend.repository.ProductCategoryRepository;
import com.example.shop_backend.repository.ProductImageRepository;
import com.example.shop_backend.repository.ProductLabelRepository;
import com.example.shop_backend.repository.ProductRepository;
import com.example.shop_backend.repository.ProductVariantImageRepository;
import com.example.shop_backend.repository.ProductVariantRepository;
import com.example.shop_backend.repository.SizeRepository;

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
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
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
     *    - stock: từ request
     * 3. Lưu product vào database -> nhận ID tự động
     * 4. Thêm categories: Duyệt categoryIds và tạo ProductCategory
     * 5. Thêm labels: Duyệt labelIds và tạo ProductLabel
     * 6. Thêm images: Duyệt images và tạo ProductImage
     *    - displayOrder bắt đầu từ 1
     *    - Ảnh đầu tiên tự động là thumbnail
     * 7. Thêm variants (nếu có):
     *    - Tự động generate SKU: "VAR-{productId}-{counter}"
     *    - Tính tổng stock từ tất cả variants
     *    - Thêm variant images cho mỗi variant
     *    - Set total_product = tổng stock của variants
     * 8. Nếu không có variants: Set total_product = stock của product
     * 9. Trả về ProductResponse của sản phẩm vừa tạo
     */
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        // Get brand
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        // Create product
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .costPrice(null) // Cost price is null if not provided
                .discountPercent(request.getDiscountPercent() != null ? request.getDiscountPercent() : 0)
                .brand(brand)
                .stock(request.getStock())
                .build();

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
            int displayOrder = 1;
            for (CreateProductRequest.ProductImageRequest imageReq : request.getImages()) {
                ProductImage productImage = ProductImage.builder()
                        .product(product)
                        .imageUrl(imageReq.getImageUrl())
                        .altText(imageReq.getAltText())
                        .isThumbnail(displayOrder == 1) // First image is thumbnail
                        .displayOrder(displayOrder)
                        .build();
                
                productImageRepository.save(productImage);
                displayOrder++;
            }
        }

        // Add variants
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            int totalVariantStock = 0;
            int variantCounter = 1;
            
            for (CreateProductRequest.ProductVariantRequest variantReq : request.getVariants()) {
                Color color = null;
                Size size = null;

                if (variantReq.getColorId() != null) {
                    color = colorRepository.findById(variantReq.getColorId())
                            .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND));
                }

                if (variantReq.getSizeId() != null) {
                    size = sizeRepository.findById(variantReq.getSizeId())
                            .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_FOUND));
                }

                // Auto-generate variant SKU
                String variantSku = "VAR-" + product.getId() + "-" + variantCounter;

                BigDecimal variantPrice = variantReq.getPrice() != null ? variantReq.getPrice() : product.getPrice();

                ProductVariant productVariant = ProductVariant.builder()
                        .product(product)
                        .color(color)
                        .size(size)
                        .sku(variantSku)
                        .stock(variantReq.getStock())
                        .price(variantPrice)
                        .build();

                productVariant = productVariantRepository.save(productVariant);
                totalVariantStock += variantReq.getStock();
                variantCounter++;

                // Add variant images
                if (variantReq.getImages() != null && !variantReq.getImages().isEmpty()) {
                    for (String imageUrl : variantReq.getImages()) {
                        ProductVariantImage variantImage = ProductVariantImage.builder()
                                .productVariant(productVariant)
                                .imageUrl(imageUrl)
                                .build();
                        
                        productVariantImageRepository.save(variantImage);
                    }
                }
            }
            
            // Set total_product = total stock from variants (initial creation)
            product.setTotalProduct(totalVariantStock);
            productRepository.save(product);
        } else {
            // No variants, use product stock
            product.setTotalProduct(request.getStock());
            productRepository.save(product);
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
     * @throws AppException nếu product/brand/category/label/color/size không tồn tại
     * 
     * Logic:
     * 1. Tìm product hiện tại theo ID
     * 2. Validate và lấy brand mới
     * 3. Cập nhật thông tin cơ bản:
     *    - name, description, price, stock: từ request
     *    - costPrice: set NULL
     *    - discountPercent: từ request hoặc 0
     * 4. Cập nhật categories:
     *    - Xóa TẤT CẢ categories cũ
     *    - Gọi flush() để đảm bảo xóa hoàn tất trước khi insert
     *    - Thêm categories mới từ request
     * 5. Cập nhật labels (tương tự categories)
     * 6. Cập nhật images (tương tự)
     * 7. Cập nhật variants:
     *    - Tính sold items = total_product - old total stock
     *    - Xóa variant images cũ trước
     *    - Xóa variants cũ và flush()
     *    - Thêm variants mới với SKU từ request
     *    - Tính new total stock từ variants mới
     *    - Update total_product = new stock + sold items (giữ nguyên số lượng đã bán)
     * 8. Trả về ProductResponse đã update
     * 
     * Note: flush() rất quan trọng để tránh lỗi duplicate key khi xóa và insert lại
     */
    @Transactional
    public ProductResponse updateProduct(Integer id, UpdateProductRequest request) {
        // Get existing product
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Get brand
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        // Update product fields
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCostPrice(null); // Cost price is null if not provided
        product.setDiscountPercent(request.getDiscountPercent() != null ? request.getDiscountPercent() : 0);
        product.setBrand(brand);
        product.setStock(request.getStock());

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
            List<ProductImage> oldImages = productImageRepository.findByProductIdOrderByDisplayOrderAsc(id);
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
                        .isThumbnail(imageReq.getIsThumbnail())
                        .displayOrder(imageReq.getDisplayOrder())
                        .build();
                
                productImageRepository.save(productImage);
            }
        }

        // Update variants: Xóa cũ và thêm mới
        if (request.getVariants() != null) {
            // Get old total stock before deletion
            int oldTotalStock = 0;
            List<ProductVariant> oldVariants = productVariantRepository.findByProductId(id);
            for (ProductVariant oldVariant : oldVariants) {
                oldTotalStock += oldVariant.getStock();
                // Xóa variant images trước
                List<ProductVariantImage> variantImages = productVariantImageRepository.findByProductVariantId(oldVariant.getId());
                if (!variantImages.isEmpty()) {
                    productVariantImageRepository.deleteAll(variantImages);
                }
            }
            productVariantRepository.deleteAll(oldVariants);
            productVariantRepository.flush(); // Force delete trước khi insert

            // Calculate sold items from old data
            int soldItems = product.getTotalProduct() - oldTotalStock;
            
            // Thêm variants mới và tính tổng stock mới
            int newTotalStock = 0;
            for (UpdateProductRequest.ProductVariantRequest variantReq : request.getVariants()) {
                Color color = null;
                Size size = null;

                if (variantReq.getColorId() != null) {
                    color = colorRepository.findById(variantReq.getColorId())
                            .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND));
                }

                if (variantReq.getSizeId() != null) {
                    size = sizeRepository.findById(variantReq.getSizeId())
                            .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_FOUND));
                }

                BigDecimal variantPrice = variantReq.getPrice() != null ? variantReq.getPrice() : product.getPrice();

                ProductVariant productVariant = ProductVariant.builder()
                        .product(product)
                        .color(color)
                        .size(size)
                        .sku(variantReq.getSku())
                        .stock(variantReq.getStock())
                        .price(variantPrice)
                        .build();

                productVariant = productVariantRepository.save(productVariant);
                newTotalStock += variantReq.getStock();

                // Add variant images
                if (variantReq.getImages() != null && !variantReq.getImages().isEmpty()) {
                    for (String imageUrl : variantReq.getImages()) {
                        ProductVariantImage variantImage = ProductVariantImage.builder()
                                .productVariant(productVariant)
                                .imageUrl(imageUrl)
                                .build();
                        
                        productVariantImageRepository.save(variantImage);
                    }
                }
            }
            
            // Update total_product = new stock + sold items
            product.setTotalProduct(newTotalStock + soldItems);
            productRepository.save(product);
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
        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(id);
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