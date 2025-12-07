package com.example.shop_backend.specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.shop_backend.model.Product;
import com.example.shop_backend.model.ProductCategory;
import com.example.shop_backend.model.enums.ProductSex;
import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {

    /**
     * Tìm kiếm theo tên sản phẩm (search)
     */
    public static Specification<Product> hasNameContaining(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")), 
                "%" + search.toLowerCase() + "%"
            );
        };
    }

    /**
     * Lọc theo danh mục (OR - sản phẩm thuộc 1 trong các category)
     * Tối ưu: Dùng subquery thay vì JOIN để tránh duplicate rows
     * Hỗ trợ cả categoryId (số) và categoryName (chữ)
     */
    public static Specification<Product> hasCategories(List<String> categories) {
        return (root, query, criteriaBuilder) -> {
            if (categories == null || categories.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            // Tách categoryIds và categoryNames
            List<Integer> categoryIds = new ArrayList<>();
            List<String> categoryNames = new ArrayList<>();
            
            for (String category : categories) {
                try {
                    categoryIds.add(Integer.parseInt(category));
                } catch (NumberFormatException e) {
                    categoryNames.add(category);
                }
            }
            
            // Subquery: SELECT product_id FROM product_categories 
            // WHERE category_id IN (...) OR category_name IN (...)
            var subquery = query.subquery(Integer.class);
            var productCategoryRoot = subquery.from(ProductCategory.class);
            subquery.select(productCategoryRoot.get("product").get("id"));
            
            // Build WHERE clause
            var predicates = new ArrayList<Predicate>();
            
            if (!categoryIds.isEmpty()) {
                predicates.add(productCategoryRoot.get("category").get("id").in(categoryIds));
            }
            
            if (!categoryNames.isEmpty()) {
                predicates.add(productCategoryRoot.get("category").get("name").in(categoryNames));
            }
            
            if (!predicates.isEmpty()) {
                subquery.where(criteriaBuilder.or(predicates.toArray(new Predicate[0])));
            }
            
            return root.get("id").in(subquery);
        };
    }

    /**
     * Lọc theo giới tính (OR - sản phẩm có 1 trong các sex)
     */
    public static Specification<Product> hasSex(List<ProductSex> sexList) {
        return (root, query, criteriaBuilder) -> {
            if (sexList == null || sexList.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("sex").in(sexList);
        };
    }

    /**
     * Lọc theo thương hiệu (OR - sản phẩm thuộc 1 trong các brand)
     */
    public static Specification<Product> hasBrands(List<String> brands) {
        return (root, query, criteriaBuilder) -> {
            if (brands == null || brands.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("brand").get("name").in(brands);
        };
    }

    /**
     * Lọc theo giá tối thiểu
     */
    public static Specification<Product> hasPriceGreaterThanOrEqual(BigDecimal priceMin) {
        return (root, query, criteriaBuilder) -> {
            if (priceMin == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("discountPrice"), priceMin);
        };
    }

    /**
     * Lọc theo giá tối đa
     */
    public static Specification<Product> hasPriceLessThanOrEqual(BigDecimal priceMax) {
        return (root, query, criteriaBuilder) -> {
            if (priceMax == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("discountPrice"), priceMax);
        };
    }

    /**
     * Chỉ lấy sản phẩm active
     */
    public static Specification<Product> isActive() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("active"), true);
    }

    /**
     * Lọc theo khoảng thời gian tạo sản phẩm (createdAt >= startDate)
     */
    public static Specification<Product> createdAfterOrEqual(LocalDate startDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null) {
                return criteriaBuilder.conjunction();
            }
            LocalDateTime startDateTime = startDate.atStartOfDay();
            return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDateTime);
        };
    }

    /**
     * Lọc theo khoảng thời gian tạo sản phẩm (createdAt <= endDate)
     */
    public static Specification<Product> createdBeforeOrEqual(LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (endDate == null) {
                return criteriaBuilder.conjunction();
            }
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDateTime);
        };
    }

    /**
     * Combine tất cả filters với AND, nhưng trong mỗi filter dùng OR
     */
    public static Specification<Product> filterProducts(
            String search,
            List<String> categories,
            List<ProductSex> sexList,
            List<String> brands,
            BigDecimal priceMin,
            BigDecimal priceMax,
            Boolean activeOnly) {
        
        Specification<Product> spec = Specification.where(null);
        
        // AND: search
        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and(hasNameContaining(search));
        }
        
        // AND: categories (OR trong categories)
        if (categories != null && !categories.isEmpty()) {
            spec = spec.and(hasCategories(categories));
        }
        
        // AND: sex (OR trong sexList)
        if (sexList != null && !sexList.isEmpty()) {
            spec = spec.and(hasSex(sexList));
        }
        
        // AND: brands (OR trong brands)
        if (brands != null && !brands.isEmpty()) {
            spec = spec.and(hasBrands(brands));
        }
        
        // AND: priceMin
        if (priceMin != null) {
            spec = spec.and(hasPriceGreaterThanOrEqual(priceMin));
        }
        
        // AND: priceMax
        if (priceMax != null) {
            spec = spec.and(hasPriceLessThanOrEqual(priceMax));
        }
        
        // AND: active
        if (activeOnly != null && activeOnly) {
            spec = spec.and(isActive());
        }
        
        return spec;
    }

    /**
     * Combine tất cả filters với AND + thêm date range filter
     */
    public static Specification<Product> filterProductsWithDateRange(
            String search,
            List<String> categories,
            List<ProductSex> sexList,
            List<String> brands,
            BigDecimal priceMin,
            BigDecimal priceMax,
            LocalDate startDate,
            LocalDate endDate,
            Boolean activeOnly) {
        
        // Gọi filterProducts base
        Specification<Product> spec = filterProducts(
            search, categories, sexList, brands, priceMin, priceMax, activeOnly
        );
        
        // AND: startDate
        if (startDate != null) {
            spec = spec.and(createdAfterOrEqual(startDate));
        }
        
        // AND: endDate
        if (endDate != null) {
            spec = spec.and(createdBeforeOrEqual(endDate));
        }
        
        return spec;
    }
}
