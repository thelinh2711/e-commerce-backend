# 📖 HƯỚNG DẪN TEST API PRODUCT

Base URL: `http://localhost:8080`

**⚠️ Lưu ý:** Tất cả URL ảnh trong response sẽ có dạng: `{baseUrl}/{imagePath}`  
Ví dụ: `http://localhost:8080/public/product_image/nike_peg40_1.jpg`

---

## 📋 MỤC LỤC
1. [Setup & Authentication](#1-setup--authentication)
2. [GET - Lấy danh sách sản phẩm](#2-get---lấy-danh-sách-sản-phẩm)
3. [GET - Lấy chi tiết sản phẩm](#3-get---lấy-chi-tiết-sản-phẩm)
4. [POST - Tạo sản phẩm mới](#4-post---tạo-sản-phẩm-mới)
5. [PUT - Cập nhật sản phẩm](#5-put---cập-nhật-sản-phẩm)
6. [DELETE - Xóa sản phẩm](#6-delete---xóa-sản-phẩm)

---

## 1. SETUP & AUTHENTICATION

### 1.1. Đăng nhập để lấy Access Token (ADMIN)

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "admin@shop.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "code": 1000,
  "message": "Đăng nhập thành công",
  "result": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "email": "admin@shop.com",
      "fullName": "Nguyễn Văn Quản Trị",
      "role": "ADMIN"
    }
  }
}
```

**⚠️ Lưu ý:** Lưu lại `accessToken` để sử dụng cho các request cần authentication (POST/PUT/DELETE)

---

## 2. GET - LẤY DANH SÁCH SẢN PHẨM

### Endpoint
```
GET /api/products
```

### Request (Postman/Thunder Client)

**Method:** GET  
**URL:** `http://localhost:8080/api/products`  
**Headers:** Không cần

### Curl Command
```bash
curl -X GET http://localhost:8080/api/products
```

### Response Success (200 OK)
```json
{
  "success": true,
  "data": [
    {
      "id": "1",
      "name": "Nike Air Zoom Pegasus 40",
      "slug": "nike-air-zoom-pegasus-40",
      "brand": "Nike",
      "price": {
        "current": 3200000.00,
        "original": 3200000.00,
        "discount_percent": 0,
        "currency": "VND"
      },
      "images": [
        "http://localhost:8080/public/product_image/nike_peg40_1.jpg",
        "http://localhost:8080/public/product_image/nike_peg40_2.jpg"
      ],
      "variants": [
        {
          "color_name": "Đen",
          "color_hex": "#000000",
          "size": "40",
        "image": "http://localhost:8080/public/product_image/nike_peg40_black.jpg",
          "stock": 20
        },
        {
          "color_name": "Đen",
          "color_hex": "#000000",
          "size": "41",
          "image": "http://localhost:8080/public/product_image/nike_peg40_blk.jpg",
          "stock": 20
        }
      ],
      "total_count": 105,
      "sold": 45,
      "created_at": "2025-09-15T10:00:00"
    },
    {
      "id": "3",
      "name": "Áo Thun Nike Dri-FIT Miller",
      "slug": "ao-thun-nike-dri-fit-miller",
      "brand": "Nike",
      "price": {
        "current": 850000.00,
        "original": 850000.00,
        "discount_percent": 0,
        "currency": "VND"
      },
      "images": [
        "http://localhost:8080/public/product_image/nike_miller_1.jpg",
        "http://localhost:8080/public/product_image/nike_miller_2.jpg"
      ],
      "variants": [
        {
          "color_name": "Đen",
          "color_hex": "#000000",
          "size": "S",
          "image": "",
          "stock": 30
        },
        {
          "color_name": "Đen",
          "color_hex": "#000000",
          "size": "M",
          "image": "",
          "stock": 30
        }
      ],
      "total_count": 165,
      "sold": 135,
      "created_at": "2025-10-01T09:15:00"
    }
  ]
}
```

---

## 3. GET - LẤY CHI TIẾT SẢN PHẨM

### Endpoint
```
GET /api/products/{id}
```

### Request (Postman/Thunder Client)

**Method:** GET  
**URL:** `http://localhost:8080/api/products/3`  
**Headers:** Không cần

### Curl Command
```bash
curl -X GET http://localhost:8080/api/products/3
```

### Response Success (200 OK)
```json
{
  "code": 1000,
  "message": "Lấy sản phẩm thành công",
  "result": {
    "id": "3",
    "name": "Áo Thun Nike Dri-FIT Miller",
    "slug": "ao-thun-nike-dri-fit-miller",
    "brand": "Nike",
    "price": {
      "current": 850000.00,
      "original": 850000.00,
      "discount_percent": 0,
      "currency": "VND"
    },
    "images": [
      "http://localhost:8080/public/product_image/nike_peg40_1.jpg",
      "http://localhost:8080/public/product_image/nike_peg40_2.jpg"
    ],
    "variants": [
      {
        "color_name": "Đen",
        "color_hex": "#000000",
        "size": "S",
        "image": "",
        "stock": 30
      },
      {
        "color_name": "Đen",
        "color_hex": "#000000",
        "size": "M",
        "image": "",
        "stock": 30
      },
      {
        "color_name": "Đen",
        "color_hex": "#000000",
        "size": "L",
        "image": "",
        "stock": 30
      },
      {
        "color_name": "Trắng",
        "color_hex": "#FFFFFF",
        "size": "S",
        "image": "",
        "stock": 25
      },
      {
        "color_name": "Trắng",
        "color_hex": "#FFFFFF",
        "size": "M",
        "image": "",
        "stock": 25
      },
      {
        "color_name": "Trắng",
        "color_hex": "#FFFFFF",
        "size": "L",
        "image": "",
        "stock": 25
      }
    ],
    "total_count": 165,
    "sold": 135,
    "created_at": "2025-10-01T09:15:00"
  }
}
```

### Response Error (404 Not Found)
```json
{
  "code": 1004,
  "message": "Sản phẩm không tồn tại"
}
```

---

## 4. POST - TẠO SẢN PHẨM MỚI (ADMIN ONLY)

### Endpoint
```
POST /api/products
```

### Request (Postman/Thunder Client)

**Method:** POST  
**URL:** `http://localhost:8080/api/products`  
**Headers:**
- `Content-Type: application/json`
- `Authorization: Bearer YOUR_ACCESS_TOKEN`

**Body (JSON):**

#### Ví dụ 1: Sản phẩm có variants (Giày/Áo/Quần)
```json
{
  "name": "Áo Thun Adidas Test Performance",
  "description": "Áo thun thể thao cao cấp, thấm hút mồ hôi tốt",
  "price": 950000,
  "discountPrice": null,
  "discountPercent": 0,
  "brandId": 2,
  "sku": "ADI-TEST-001",
  "stock": 0,
  "slug": "ao-thun-adidas-test-performance",
  "status": "ACTIVE",
  "categoryIds": [3],
  "labelIds": [1, 2],
  "images": [
    {
      "imageUrl": "public/product_image/adidas_test_1.jpg",
      "altText": "Áo Adidas Test - Ảnh 1",
      "isThumbnail": true,
      "displayOrder": 1
    },
    {
      "imageUrl": "public/product_image/adidas_test_2.jpg",
      "altText": "Áo Adidas Test - Ảnh 2",
      "isThumbnail": false,
      "displayOrder": 2
    }
  ],
  "variants": [
    {
      "colorId": 1,
      "sizeId": 1,
      "sku": "ADI-TEST-001-BLK-S",
      "stock": 30,
      "price": 950000,
      "images": ["public/product_image/adidas_test_black.jpg"]
    },
    {
      "colorId": 1,
      "sizeId": 2,
      "sku": "ADI-TEST-001-BLK-M",
      "stock": 50,
      "price": 950000,
      "images": ["public/product_image/adidas_test_black.jpg"]
    },
    {
      "colorId": 1,
      "sizeId": 3,
      "sku": "ADI-TEST-001-BLK-L",
      "stock": 40,
      "price": 950000,
      "images": ["public/product_image/adidas_test_black.jpg"]
    },
    {
      "colorId": 2,
      "sizeId": 1,
      "sku": "ADI-TEST-001-WHT-S",
      "stock": 25,
      "price": 950000,
      "images": ["public/product_image/adidas_test_white.jpg"]
    },
    {
      "colorId": 2,
      "sizeId": 2,
      "sku": "ADI-TEST-001-WHT-M",
      "stock": 45,
      "price": 950000,
      "images": ["public/product_image/adidas_test_white.jpg"]
    },
    {
      "colorId": 2,
      "sizeId": 3,
      "sku": "ADI-TEST-001-WHT-L",
      "stock": 35,
      "price": 950000,
      "images": ["public/product_image/adidas_test_white.jpg"]
    }
  ]
}
```

#### Ví dụ 2: Sản phẩm không có variants (Phụ kiện)
```json
{
  "name": "Balo Nike Brasilia Test",
  "description": "Balo thể thao đa năng",
  "price": 1200000,
  "discountPrice": 1000000,
  "discountPercent": 17,
  "brandId": 1,
  "sku": "NIKE-BALO-TEST",
  "stock": 50,
  "slug": "balo-nike-brasilia-test",
  "status": "ACTIVE",
  "categoryIds": [9],
  "labelIds": [3],
  "images": [
    {
      "imageUrl": "public/product_image/nike_balo_test.jpg",
      "altText": "Balo Nike Test",
      "isThumbnail": true,
      "displayOrder": 1
    }
  ],
  "variants": []
}
```

### Curl Command
```bash
curl -X POST http://localhost:8080/api/products \
-H "Content-Type: application/json" \
-H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
-d '{
  "name": "Áo Thun Adidas Test Performance",
  "description": "Áo thun thể thao cao cấp",
  "price": 950000,
  "brandId": 2,
  "sku": "ADI-TEST-001",
  "stock": 0,
  "slug": "ao-thun-adidas-test-performance",
  "status": "ACTIVE",
  "categoryIds": [3],
  "labelIds": [1, 2],
  "images": [
    {
      "imageUrl": "public/product_image/adidas_test_1.jpg",
      "altText": "Áo Adidas Test - Ảnh 1",
      "isThumbnail": true,
      "displayOrder": 1
    }
  ],
  "variants": [
    {
      "colorId": 1,
      "sizeId": 1,
      "sku": "ADI-TEST-001-BLK-S",
      "stock": 30,
      "price": 950000
    },
    {
      "colorId": 1,
      "sizeId": 2,
      "sku": "ADI-TEST-001-BLK-M",
      "stock": 50,
      "price": 950000
    }
  ]
}'
```

### Response Success (201 Created)
```json
{
  "code": 1000,
  "message": "Tạo sản phẩm thành công",
  "result": {
    "id": "101",
    "name": "Áo Thun Adidas Test Performance",
    "slug": "ao-thun-adidas-test-performance",
    "brand": "Adidas",
    "price": {
      "current": 950000.00,
      "original": 950000.00,
      "discount_percent": 0,
      "currency": "VND"
    },
    "images": [
      "http://localhost:8080/public/product_image/adidas_test_1.jpg",
      "http://localhost:8080/public/product_image/adidas_test_2.jpg"
    ],
    "variants": [
      {
        "color_name": "Đen",
        "color_hex": "#000000",
        "size": "S",
        "image": "http://localhost:8080/public/product_image/adidas_test_black.jpg",
        "stock": 30
      },
      {
        "color_name": "Đen",
        "color_hex": "#000000",
        "size": "M",
        "image": "http://localhost:8080/public/product_image/adidas_test_black.jpg",
        "stock": 50
      }
    ],
    "total_count": 225,
    "sold": 0,
    "created_at": "2025-11-06T10:30:00"
  }
}
```

### Response Errors

**401 Unauthorized (Không có token)**
```json
{
  "code": 1006,
  "message": "Người dùng chưa được xác thực"
}
```

**403 Forbidden (Không phải ADMIN)**
```json
{
  "code": 1002,
  "message": "Bạn không có quyền truy cập"
}
```

**400 Bad Request (Validation Error)**
```json
{
  "code": 1003,
  "message": "Dữ liệu không hợp lệ",
  "errors": {
    "name": "Tên sản phẩm không được để trống",
    "price": "Giá sản phẩm không được để trống",
    "sku": "SKU không được để trống"
  }
}
```

**409 Conflict (Slug đã tồn tại)**
```json
{
  "code": 1005,
  "message": "Slug đã tồn tại"
}
```

---

## 5. PUT - CẬP NHẬT SẢN PHẨM (ADMIN ONLY)

### Endpoint
```
PUT /api/products/{id}
```

### Request (Postman/Thunder Client)

**Method:** PUT  
**URL:** `http://localhost:8080/api/products/101`  
**Headers:**
- `Content-Type: application/json`
- `Authorization: Bearer YOUR_ACCESS_TOKEN`

**Body (JSON):**

#### Ví dụ: Cập nhật sản phẩm và nhập thêm hàng
```json
{
  "name": "Áo Thun Adidas Test Performance - Updated",
  "description": "Áo thun thể thao cao cấp, phiên bản cải tiến",
  "price": 990000,
  "discountPrice": 850000,
  "discountPercent": 14,
  "brandId": 2,
  "sku": "ADI-TEST-001-V2",
  "stock": 0,
  "slug": "ao-thun-adidas-test-performance",
  "status": "ACTIVE",
  "categoryIds": [3, 7],
  "labelIds": [1, 2, 3],
  "images": [
    {
      "imageUrl": "public/product_image/adidas_test_updated_1.jpg",
      "altText": "Áo Adidas Test Updated - Ảnh 1",
      "isThumbnail": true,
      "displayOrder": 1
    },
    {
      "imageUrl": "public/product_image/adidas_test_updated_2.jpg",
      "altText": "Áo Adidas Test Updated - Ảnh 2",
      "isThumbnail": false,
      "displayOrder": 2
    }
  ],
  "variants": [
    {
      "colorId": 1,
      "sizeId": 1,
      "sku": "ADI-TEST-001-BLK-S",
      "stock": 50,
      "price": 990000,
      "images": ["public/product_image/adidas_test_black_v2.jpg"]
    },
    {
      "colorId": 1,
      "sizeId": 2,
      "sku": "ADI-TEST-001-BLK-M",
      "stock": 70,
      "price": 990000,
      "images": ["public/product_image/adidas_test_black_v2.jpg"]
    },
    {
      "colorId": 1,
      "sizeId": 3,
      "sku": "ADI-TEST-001-BLK-L",
      "stock": 60,
      "price": 990000,
      "images": ["public/product_image/adidas_test_black_v2.jpg"]
    },
    {
      "colorId": 2,
      "sizeId": 1,
      "sku": "ADI-TEST-001-WHT-S",
      "stock": 40,
      "price": 990000,
      "images": ["public/product_image/adidas_test_white_v2.jpg"]
    },
    {
      "colorId": 2,
      "sizeId": 2,
      "sku": "ADI-TEST-001-WHT-M",
      "stock": 60,
      "price": 990000,
      "images": ["public/product_image/adidas_test_white_v2.jpg"]
    },
    {
      "colorId": 2,
      "sizeId": 3,
      "sku": "ADI-TEST-001-WHT-L",
      "stock": 50,
      "price": 990000,
      "images": ["public/product_image/adidas_test_white_v2.jpg"]
    },
    {
      "colorId": 3,
      "sizeId": 1,
      "sku": "ADI-TEST-001-RED-S",
      "stock": 30,
      "price": 990000,
      "images": ["public/product_image/adidas_test_red_v2.jpg"]
    },
    {
      "colorId": 3,
      "sizeId": 2,
      "sku": "ADI-TEST-001-RED-M",
      "stock": 40,
      "price": 990000,
      "images": ["public/product_image/adidas_test_red_v2.jpg"]
    },
    {
      "colorId": 3,
      "sizeId": 3,
      "sku": "ADI-TEST-001-RED-L",
      "stock": 35,
      "price": 990000,
      "images": ["public/product_image/adidas_test_red_v2.jpg"]
    }
  ]
}
```

### Curl Command
```bash
curl -X PUT http://localhost:8080/api/products/101 \
-H "Content-Type: application/json" \
-H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
-d '{
  "name": "Áo Thun Adidas Test Performance - Updated",
  "description": "Áo thun thể thao cao cấp, phiên bản cải tiến",
  "price": 990000,
  "discountPrice": 850000,
  "discountPercent": 14,
  "brandId": 2,
  "sku": "ADI-TEST-001-V2",
  "stock": 0,
  "slug": "ao-thun-adidas-test-performance",
  "status": "ACTIVE",
  "categoryIds": [3, 7],
  "labelIds": [1, 2, 3],
  "images": [
    {
      "imageUrl": "public/product_image/adidas_test_updated_1.jpg",
      "altText": "Áo Adidas Test Updated",
      "isThumbnail": true,
      "displayOrder": 1
    }
  ],
  "variants": [
    {
      "colorId": 1,
      "sizeId": 1,
      "sku": "ADI-TEST-001-BLK-S",
      "stock": 50,
      "price": 990000
    }
  ]
}'
```

### Response Success (200 OK)
```json
{
  "code": 1000,
  "message": "Cập nhật sản phẩm thành công",
  "result": {
    "id": "101",
    "name": "Áo Thun Adidas Test Performance - Updated",
    "slug": "ao-thun-adidas-test-performance",
    "brand": "Adidas",
    "price": {
      "current": 850000.00,
      "original": 990000.00,
      "discount_percent": 14,
      "currency": "VND"
    },
    "images": [
      "public/product_image/adidas_test_updated_1.jpg",
      "public/product_image/adidas_test_updated_2.jpg"
    ],
    "variants": [
      {
        "color_name": "Đen",
        "color_hex": "#000000",
        "size": "S",
        "image": "public/product_image/adidas_test_black_v2.jpg",
        "stock": 50
      },
      {
        "color_name": "Đỏ",
        "color_hex": "#FF0000",
        "size": "L",
        "image": "public/product_image/adidas_test_red_v2.jpg",
        "stock": 35
      }
    ],
    "total_count": 435,
    "sold": 0,
    "created_at": "2025-11-06T10:30:00"
  }
}
```

**📊 Giải thích logic cập nhật:**
- **Trước khi update:** `total_count = 225`, `sold = 0`
- **Sau khi update:** `total_count = 435`, `sold = 0`
- **total_product** = 435 (tổng stock mới từ 9 variants)

---

## 6. DELETE - XÓA SẢN PHẨM (ADMIN ONLY)

### Endpoint
```
DELETE /api/products/{id}
```

### Request (Postman/Thunder Client)

**Method:** DELETE  
**URL:** `http://localhost:8080/api/products/101`  
**Headers:**
- `Authorization: Bearer YOUR_ACCESS_TOKEN`

### Curl Command
```bash
curl -X DELETE http://localhost:8080/api/products/101 \
-H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Response Success (200 OK)
```json
{
  "code": 1000,
  "message": "Xóa sản phẩm thành công"
}
```

### Response Error (404 Not Found)
```json
{
  "code": 1004,
  "message": "Sản phẩm không tồn tại"
}
```

### Response Error (403 Forbidden)
```json
{
  "code": 1002,
  "message": "Bạn không có quyền truy cập"
}
```

---

## 📝 LƯU Ý QUAN TRỌNG

### 1. **Authentication**
- GET không cần token
- POST/PUT/DELETE **BẮT BUỘC** phải có token ADMIN
- Token format: `Authorization: Bearer YOUR_ACCESS_TOKEN`

### 2. **IDs cần thiết** (từ database init_data.sql)

#### Brand IDs:
- 1: Nike
- 2: Adidas
- 3: Puma
- 4: Under Armour
- 5: ASICS

#### Category IDs:
- 1: Giày Chạy Bộ
- 2: Giày Đá Banh
- 3: Áo Thun & Áo Ba Lỗ
- 4: Quần Short
- 5: Quần Dài & Legging
- 6: Áo Khoác
- 7: Đồ Tập Gym
- 8: Phụ Kiện
- 9: Balo & Túi
- 10: Dụng Cụ Thể Thao

#### Color IDs:
- 1: Đen (#000000)
- 2: Trắng (#FFFFFF)
- 3: Đỏ (#FF0000)
- 4: Xanh Dương (#0000FF)
- 5: Xám (#808080)

#### Size IDs:
- 1: S
- 2: M
- 3: L
- 4: XL
- 5: XXL
- 6: 39 (giày)
- 7: 40 (giày)
- 8: 41 (giày)
- 9: 42 (giày)
- 10: 43 (giày)

#### Label IDs:
- 1: Hàng Mới
- 2: Bán Chạy
- 3: Giảm Giá
- 4: Độc Quyền
- 5: Thân Thiện Môi Trường

### 3. **Logic tính toán**
- `total_count`: Tổng tồn kho hiện tại (tính từ tất cả variants)
- `sold`: Số lượng đã bán = total_product - total_count
- `total_product`: Được tự động tính khi tạo/cập nhật

### 4. **Validation Rules**
- `name`: không được trống
- `price`: >= 0, bắt buộc
- `brandId`: bắt buộc, phải tồn tại trong DB
- `sku`: không được trống, unique
- `slug`: không được trống, unique
- `stock`: >= 0, bắt buộc
- Mỗi variant phải có `sku` và `stock`

---

## 🔧 TESTING WORKFLOW ĐỀ XUẤT

### Bước 1: Test GET (không cần auth)
```bash
# Lấy danh sách
curl -X GET http://localhost:8080/api/products

# Lấy chi tiết
curl -X GET http://localhost:8080/api/products/1
```

### Bước 2: Đăng nhập lấy token
```bash
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{"email":"admin@shop.com","password":"password123"}'
```

### Bước 3: Test POST (tạo sản phẩm)
```bash
curl -X POST http://localhost:8080/api/products \
-H "Content-Type: application/json" \
-H "Authorization: Bearer YOUR_TOKEN" \
-d @create_product_request.json
```

### Bước 4: Test PUT (cập nhật)
```bash
curl -X PUT http://localhost:8080/api/products/101 \
-H "Content-Type: application/json" \
-H "Authorization: Bearer YOUR_TOKEN" \
-d @update_product_request.json
```

### Bước 5: Test DELETE
```bash
curl -X DELETE http://localhost:8080/api/products/101 \
-H "Authorization: Bearer YOUR_TOKEN"
```

---

## 🎯 TEST CASES ĐỀ XUẤT

### ✅ Happy Path
1. ✓ Lấy danh sách sản phẩm
2. ✓ Lấy chi tiết 1 sản phẩm
3. ✓ Tạo sản phẩm mới có variants
4. ✓ Tạo sản phẩm không có variants
5. ✓ Cập nhật sản phẩm (nhập thêm hàng)
6. ✓ Xóa sản phẩm

### ❌ Error Cases
1. ✗ GET sản phẩm không tồn tại (404)
2. ✗ POST không có token (401)
3. ✗ POST với user thường (403)
4. ✗ POST với slug trùng (409)
5. ✗ POST thiếu field bắt buộc (400)
6. ✗ PUT sản phẩm không tồn tại (404)
7. ✗ DELETE không có quyền (403)

---

## 📚 TOOLS ĐỀ XUẤT

1. **Postman** - GUI, dễ sử dụng
2. **Thunder Client** (VS Code Extension) - nhẹ, tích hợp IDE
3. **cURL** - command line, test nhanh
4. **REST Client** (VS Code Extension) - test trong file .http

**Chúc bạn test thành công! 🚀**
