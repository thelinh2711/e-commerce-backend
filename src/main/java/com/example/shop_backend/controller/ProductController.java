package com.example.shop_backend.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop_backend.dto.request.CreateProductRequest;
import com.example.shop_backend.dto.request.UpdateProductRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.ProductListResponse;
import com.example.shop_backend.dto.response.ProductResponse;
import com.example.shop_backend.dto.response.ProductSearchResponse;
import com.example.shop_backend.model.User;
import com.example.shop_backend.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/products
     * GET /api/products?active=true
     * GET /api/products?active=false
     * GET /api/products?id=10 (lấy 12 sản phẩm từ id=10)
     * GET /api/products?id=10&size=20 (lấy 20 sản phẩm từ id=10)
     */
    @GetMapping
    public ResponseEntity<ProductListResponse> getAllProducts(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false, defaultValue = "12") Integer size,
            @AuthenticationPrincipal User currentUser) {
        
        List<ProductResponse> products = productService.getAllProducts(active, id, size, currentUser);
        
        ProductListResponse response = ProductListResponse.builder()
                .success(true)
                .data(products)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/products/search
     * Tìm kiếm và lọc sản phẩm với phân trang
     * 
     * Logic lọc:
     * - Cùng trường (category, sex, brand): dùng OR
     * - Khác trường: dùng AND
     * 
     * Ví dụ: category=áo-thun&category=áo-polo&sex=male&brand=Nike
     * => (category = áo-thun OR áo-polo) AND sex = male AND brand = Nike
     * 
     * @param search - từ khóa tìm kiếm (tên sản phẩm)
     * @param category - danh sách category (truyền nhiều lần, ví dụ: category=áo-thun&category=áo-polo)
     * @param sex - danh sách giới tính (male, female, unisex)
     * @param brand - danh sách thương hiệu (truyền nhiều lần)
     * @param priceMin - giá tối thiểu (VND)
     * @param priceMax - giá tối đa (VND)
     * @param sort - kiểu sắp xếp (default: A→Z, name-desc: Z→A, price-asc: thấp→cao, price-desc: cao→thấp)
     * @param page - trang hiện tại (1-based, mặc định 1)
     * @param size - số item mỗi trang (mặc định 12)
     */
    @GetMapping("/search")
    public ResponseEntity<ProductSearchResponse> searchProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) List<String> sex,
            @RequestParam(required = false) List<String> brand,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false, defaultValue = "default") String sort,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "12") Integer size,
            @AuthenticationPrincipal User currentUser) {
        
        ProductSearchResponse response = productService.searchProducts(
                search, category, sex, brand, priceMin, priceMax, sort, page, size, currentUser);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/products/bestseller
     * Lấy sản phẩm bán chạy với tất cả filter giống search + sort theo sold
     * @param search - từ khóa tìm kiếm
     * @param category - danh sách category (OR)
     * @param sex - danh sách giới tính (OR)
     * @param brand - danh sách brand (OR)
     * @param priceMin - giá tối thiểu
     * @param priceMax - giá tối đa
     * @param startDate - ngày bắt đầu (yyyy-MM-dd, optional)
     * @param endDate - ngày kết thúc (yyyy-MM-dd, optional)
     * @param sort - kiểu sắp xếp sold (sold-asc: ít→nhiều, sold-desc: nhiều→ít, default: sold-desc)
     * @param page - trang hiện tại (1-based, mặc định 1)
     * @param size - số item mỗi trang (mặc định 12)
     */
    @GetMapping("/bestseller")
    public ResponseEntity<ProductSearchResponse> getBestseller(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) List<String> sex,
            @RequestParam(required = false) List<String> brand,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false, defaultValue = "sold-desc") String sort,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "12") Integer size,
            @AuthenticationPrincipal User currentUser) {
        
        ProductSearchResponse response = productService.getBestseller(
                search, category, sex, brand, priceMin, priceMax, startDate, endDate, sort, page, size, currentUser);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable Integer id,
            @AuthenticationPrincipal User currentUser) {
        
        ProductResponse product = productService.getProductById(id, currentUser);
        
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .code(1000)
                .message("Lấy sản phẩm thành công")
                .result(product)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @ModelAttribute CreateProductRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        ProductResponse product = productService.createProduct(request, currentUser);
        
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .code(1000)
                .message("Tạo sản phẩm thành công")
                .result(product)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Integer id,
            @Valid @ModelAttribute UpdateProductRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        ProductResponse product = productService.updateProduct(id, request, currentUser);
        
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .code(1000)
                .message("Cập nhật sản phẩm thành công")
                .result(product)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(1000)
                .message("Xóa sản phẩm thành công")
                .build();
        
        return ResponseEntity.ok(response);
    }
}