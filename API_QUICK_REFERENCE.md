# 📝 Product & Variant API - Quick Reference

## 🔑 Endpoints Summary

### Product API

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/products` | PUBLIC | Lấy danh sách tất cả sản phẩm |
| GET | `/api/products/{id}` | PUBLIC | Lấy chi tiết 1 sản phẩm |
| POST | `/api/products` | ADMIN | Tạo sản phẩm mới (có thể kèm variants) |
| PUT | `/api/products/{id}` | ADMIN | Cập nhật sản phẩm (giữ nguyên sold) |
| DELETE | `/api/products/{id}` | ADMIN | Xóa sản phẩm (cascade delete) |

### Variant API

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/product-variants/{id}` | PUBLIC | Lấy thông tin 1 variant |
| GET | `/api/product-variants/product/{id}` | PUBLIC | Lấy tất cả variants của product |
| POST | `/api/product-variants` | ADMIN | Tạo variant mới (tự động update total_product) |
| PUT | `/api/product-variants/{id}/stock` | ADMIN | Update stock variant (recalculate total_product) |
| DELETE | `/api/product-variants/{id}` | ADMIN | Xóa variant (recalculate total_product) |

---

## 📊 Data Flow Logic

### Product Creation
```
Request → Validate → Create Product → Add Categories/Labels/Images
       → Create Variants (auto SKU) → Calculate total_product
       → Response
```

**Auto-calculations:**
- `total_product` = SUM(variant stocks) hoặc product.stock (nếu không có variants)
- `stock` = SUM(variant stocks)
- `sold` = 0 (sản phẩm mới)
- SKU variants = `VAR-{productId}-{counter}`

### Product Update
```
Request → Validate → Update Product → Delete old relations
       → Add new Categories/Labels/Images
       → Delete old Variants → Create new Variants
       → Preserve sold count → Calculate new total_product
       → Response
```

**Preserve sold logic:**
```
old_sold = old_total_product - old_stock
new_total_product = new_stock + old_sold
```

### Variant Creation (Standalone)
```
Request → Validate → Check duplicate → Create Variant
       → Recalculate product.total_product
       → Response
```

**Recalculation:**
```
total_product = SUM(all variant stocks)
```

---

## 🎯 Key Features

### ✅ Product CRUD
- Tạo product với/không có variants ngay từ đầu
- Update giữ nguyên số lượng đã bán (sold)
- Cascade delete: Xóa product → Xóa tất cả variants, images, categories, labels
- Auto-generate SKU cho variants khi tạo mới
- Custom SKU cho variants khi update

### ✅ Variant CRUD
- Tạo variant standalone (sau khi product đã tồn tại)
- Update stock → Auto recalculate total_product
- Delete variant → Auto recalculate total_product
- Duplicate prevention: Không cho tạo 2 variants giống nhau (cùng product+color+size)

### ✅ Data Integrity
- `total_product` LUÔN = SUM(variant stocks) + sold items
- `sold` LUÔN = total_product - current stock
- `sold` không bao giờ âm (có logic max(0, ...))
- Update product giữ nguyên sold count

### ✅ Security
- GET endpoints: PUBLIC (ai cũng truy cập được)
- POST/PUT/DELETE: ADMIN only
- JWT token authentication

---

## 📝 Request Examples

### Create Product (Full)
```json
POST /api/products
{
  "name": "Nike Air Max",
  "description": "Giày thể thao cao cấp",
  "price": 3000000,
  "discountPercent": 10,
  "brandId": 1,
  "stock": 0,
  "categoryIds": [1, 2],
  "labelIds": [1],
  "images": [
    {
      "imageUrl": "uploads/image1.jpg",
      "altText": "Front view"
    }
  ],
  "variants": [
    {
      "colorId": 1,
      "sizeId": 2,
      "stock": 30,
      "price": 3000000,
      "images": ["uploads/variant1.jpg"]
    }
  ]
}
```

### Create Product (Minimal)
```json
POST /api/products
{
  "name": "Simple Product",
  "price": 500000,
  "brandId": 1,
  "stock": 100,
  "images": [
    {
      "imageUrl": "uploads/product.jpg",
      "altText": "Product image"
    }
  ]
}
```
> **Required fields:** name, price, brandId, stock, images (ít nhất 1 ảnh)

### Update Product
```json
PUT /api/products/{id}
{
  "name": "Updated Name",
  "price": 3500000,
  "brandId": 1,
  "stock": 0,
  "categoryIds": [1],
  "variants": [
    {
      "sku": "CUSTOM-SKU-001",
      "colorId": 1,
      "sizeId": 3,
      "stock": 50
    }
  ]
}
```

### Create Variant
```json
POST /api/product-variants
{
  "productId": 1,
  "colorId": 2,
  "sizeId": 3,
  "stock": 40,
  "price": 3200000,
  "images": ["uploads/variant.jpg"]
}
```

### Update Variant Stock
```
PUT /api/product-variants/1/stock?stock=100
```

---

## 🧪 Quick Test Commands

### Login as Admin
```bash
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@example.com\",\"password\":\"admin123\"}"
```

### Create Product
```bash
curl -X POST http://localhost:8080/api/products ^
  -H "Authorization: Bearer TOKEN" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test\",\"price\":1000000,\"brandId\":1,\"stock\":50,\"images\":[{\"imageUrl\":\"uploads/test.jpg\",\"altText\":\"Test\"}]}"
```

### Get All Products
```bash
curl http://localhost:8080/api/products
```

### Get Product by ID
```bash
curl http://localhost:8080/api/products/1
```

### Update Product
```bash
curl -X PUT http://localhost:8080/api/products/1 ^
  -H "Authorization: Bearer TOKEN" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Updated\",\"price\":1200000,\"brandId\":1,\"stock\":60}"
```

### Delete Product
```bash
curl -X DELETE http://localhost:8080/api/products/1 ^
  -H "Authorization: Bearer TOKEN"
```

### Create Variant
```bash
curl -X POST http://localhost:8080/api/product-variants ^
  -H "Authorization: Bearer TOKEN" ^
  -H "Content-Type: application/json" ^
  -d "{\"productId\":1,\"colorId\":1,\"sizeId\":2,\"stock\":30,\"price\":1000000}"
```

### Update Variant Stock
```bash
curl -X PUT "http://localhost:8080/api/product-variants/1/stock?stock=100" ^
  -H "Authorization: Bearer TOKEN"
```

### Delete Variant
```bash
curl -X DELETE http://localhost:8080/api/product-variants/1 ^
  -H "Authorization: Bearer TOKEN"
```

---

## ⚠️ Important Notes

1. **total_product Logic:**
   - Khi CREATE product: `total_product = SUM(variant.stock)`
   - Khi CREATE variant: `total_product += variant.stock`
   - Khi UPDATE variant: `total_product = recalculate từ tất cả variants`
   - Khi DELETE variant: `total_product = recalculate từ variants còn lại`
   - Khi UPDATE product: `new_total_product = new_stock + old_sold`

2. **Sold Calculation:**
   - `sold = total_product - current_stock`
   - Luôn luôn `>= 0` (có logic protection)

3. **SKU Generation:**
   - CREATE product: Auto-generate `VAR-{productId}-{counter}`
   - UPDATE product: Dùng SKU từ request (custom)
   - CREATE variant standalone: Auto-generate

4. **Cascade Delete:**
   - Xóa product → Xóa variants → Xóa variant images
   - Xóa product → Xóa product images, categories, labels

5. **Validation:**
   - Brand must exist
   - Categories must exist (if provided)
   - Labels must exist (if provided)
   - Colors must exist (if provided)
   - Sizes must exist (if provided)
   - No duplicate variants (same product+color+size)

---

## 📚 Full Documentation

Xem chi tiết tại:
- **API_TESTING_GUIDE.md** - Hướng dẫn test đầy đủ với examples
- **PRODUCT_VARIANT_API.md** - Technical documentation

---

**Version:** 1.0  
**Last Updated:** November 8, 2025
