package com.example.shop_backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop_backend.dto.request.CreateProductRequest;
import com.example.shop_backend.dto.request.UpdateProductRequest;
import com.example.shop_backend.dto.response.ProductResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
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
import com.example.shop_backend.model.enums.ProductStatus;
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

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAllActiveProducts();
        
        return products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        
        return convertToProductResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        // Validate slug uniqueness
        if (productRepository.existsBySlug(request.getSlug())) {
            throw new AppException(ErrorCode.SLUG_EXISTED);
        }

        // Get brand
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        // Parse product status
        ProductStatus status = ProductStatus.ACTIVE;
        if (request.getStatus() != null) {
            try {
                status = ProductStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_PRODUCT_STATUS);
            }
        }

        // Create product
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .discountPercent(request.getDiscountPercent() != null ? request.getDiscountPercent() : 0)
                .brand(brand)
                .sku(request.getSku())
                .stock(request.getStock())
                .slug(request.getSlug())
                .status(status)
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
            for (CreateProductRequest.ProductImageRequest imageReq : request.getImages()) {
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

        // Add variants
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            int totalVariantStock = 0;
            
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
                totalVariantStock += variantReq.getStock();

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
        
        return convertToProductResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Integer id, UpdateProductRequest request) {
        // Get existing product
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Validate slug uniqueness (nếu slug thay đổi)
        if (!product.getSlug().equals(request.getSlug()) && productRepository.existsBySlug(request.getSlug())) {
            throw new AppException(ErrorCode.SLUG_EXISTED);
        }

        // Get brand
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        // Parse product status
        ProductStatus status = ProductStatus.ACTIVE;
        if (request.getStatus() != null) {
            try {
                status = ProductStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_PRODUCT_STATUS);
            }
        }

        // Update product fields
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setDiscountPercent(request.getDiscountPercent() != null ? request.getDiscountPercent() : 0);
        product.setBrand(brand);
        product.setSku(request.getSku());
        product.setStock(request.getStock());
        product.setSlug(request.getSlug());
        product.setStatus(status);

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
        
        return convertToProductResponse(updatedProduct);
    }

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

    private ProductResponse convertToProductResponse(Product product) {
        String baseUrl = "http://localhost:8080";
        // String baseUrl = "https://fnzv9bcp-8080.asse.devtunnels.ms";

        // Get product images with base URL
        List<ProductImage> productImages = productImageRepository.findByProductIdOrderByDisplayOrderAsc(product.getId());
        List<String> imageUrls = productImages.stream()
                .map(img -> baseUrl + "/" + img.getImageUrl())
                .collect(Collectors.toList());

        // Get product variants with color and size
        List<ProductVariant> variants = productVariantRepository.findByProductIdWithColorAndSize(product.getId());
        List<ProductResponse.VariantInfo> variantInfos = new ArrayList<>();
        int totalStock = 0;

        for (ProductVariant variant : variants) {
            // Get variant image with base URL
            List<ProductVariantImage> variantImages = productVariantImageRepository.findByProductVariantId(variant.getId());
            String variantImageUrl = variantImages.isEmpty() 
                ? "" 
                : baseUrl + "/" + variantImages.get(0).getImageUrl();

            ProductResponse.VariantInfo variantInfo = ProductResponse.VariantInfo.builder()
                    .colorName(variant.getColor() != null ? variant.getColor().getName() : "")
                    .colorHex(variant.getColor() != null ? variant.getColor().getHexCode() : "")
                    .size(variant.getSize() != null ? variant.getSize().getName() : "")
                    .image(variantImageUrl)
                    .stock(variant.getStock())
                    .build();

            variantInfos.add(variantInfo);
            totalStock += variant.getStock();
        }

        // Calculate price info
        BigDecimal currentPrice = product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice();
        BigDecimal originalPrice = product.getPrice();
        Integer discountPercent = product.getDiscountPercent();

        ProductResponse.PriceInfo priceInfo = ProductResponse.PriceInfo.builder()
                .current(currentPrice)
                .original(originalPrice)
                .discountPercent(discountPercent)
                .currency("VND")
                .build();

        return ProductResponse.builder()
                .id(product.getId().toString())
                .name(product.getName())
                .slug(product.getSlug())
                .brand(product.getBrand() != null ? product.getBrand().getName() : "KHÁC")
                .price(priceInfo)
                .images(imageUrls)
                .variants(variantInfos)
                .totalCount(totalStock)
                .sold(product.getTotalProduct() - totalStock)
                .createdAt(product.getCreatedAt())
                .build();
    }
}