# 🧪 Hướng Dẫn Test API Product & Variant

## 📋 Mục lục
1. [Setup môi trường test](#setup)
2. [Authentication](#authentication)
3. [Test Product API (CRUD)](#test-product-api)
4. [Test Variant API (CRUD)](#test-variant-api)
5. [Test Cases Integration](#integration-tests)

---

## 1️⃣ Setup môi trường test {#setup}

### Yêu cầu:
- ✅ Application đang chạy: `http://localhost:8080`
- ✅ Database đã có dữ liệu init
- ✅ Tool test: Postman, Thunder Client, hoặc curl

### Kiểm tra server:
```bash
# Kiểm tra server đang chạy
curl http://localhost:8080/actuator/health

# Hoặc truy cập browser:
http://localhost:8080/actuator/health
```

---

## 2️⃣ Authentication {#authentication}

### 2.1. Login để lấy token

**Endpoint:**
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```

**Request Body - User thường:**
```json
{
  "email": "user@example.com",
  "password": "user123"
}
```

**Request Body - Admin:**
```json
{
  "email": "admin@example.com",
  "password": "admin123"
}
```

**Response Success:**
```json
{
  "code": 1000,
  "message": "Login successful",
  "result": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "email": "admin@example.com",
      "fullName": "Admin User",
      "role": "ADMIN"
    }
  }
}
```

**Lưu token:**
```
TOKEN=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 2.2. Test với curl
```bash
# Lưu token vào biến
set TOKEN=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

# Test API với token
curl -H "Authorization: Bearer %TOKEN%" http://localhost:8080/api/products
```

---

## 3️⃣ Test Product API (CRUD) {#test-product-api}

### 3.1. GET All Products (PUBLIC)

**Endpoint:**
```http
GET http://localhost:8080/api/products
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "Nike Air Zoom Pegasus 40",
      "description": "Giày chạy bộ huyền thoại, êm ái và ổn định.",
      "brand": {
        "id": 1,
        "name": "Nike"
      },
      "categories": [
        {
          "id": 1,
          "name": "Giày Chạy Bộ"
        }
      ],
      "labels": [
        {
          "id": 1,
          "name": "Best Seller"
        }
      ],
      "priceInfo": {
        "price": 3200000,
        "currency": "VND",
        "costPrice": null,
        "discountPercent": 0,
        "discountPrice": 3200000
      },
      "stock": 100,
      "sold": 50,
      "totalProduct": 150,
      "thumbnail": "nike-pegasus-40.jpg",
      "images": [
        "nike-pegasus-40-1.jpg",
        "nike-pegasus-40-2.jpg"
      ],
      "createdAt": "2025-09-15T10:00:00"
    }
  ]
}
```

**Test với Postman:**
1. Method: `GET`
2. URL: `http://localhost:8080/api/products`
3. Click **Send**
4. ✅ Expect: Status 200, list of products

**Test với curl:**
```bash
curl http://localhost:8080/api/products
```

### 3.2. GET Product by ID (PUBLIC)

**Endpoint:**
```http
GET http://localhost:8080/api/products/{id}
```

**Example:**
```http
GET http://localhost:8080/api/products/1
```

**Response:**
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": 1,
    "name": "Nike Air Zoom Pegasus 40",
    "description": "Giày chạy bộ huyền thoại, êm ái và ổn định.",
    "brand": {
      "id": 1,
      "name": "Nike"
    },
    "categories": [
      {
        "id": 1,
        "name": "Giày Chạy Bộ"
      }
    ],
    "labels": [
      {
        "id": 1,
        "name": "Best Seller"
      }
    ],
    "priceInfo": {
      "price": 3200000,
      "currency": "VND",
      "costPrice": null,
      "discountPercent": 0,
      "discountPrice": 3200000
    },
    "stock": 100,
    "sold": 50,
    "totalProduct": 150,
    "thumbnail": "nike-pegasus-40.jpg",
    "images": [
      "nike-pegasus-40-1.jpg",
      "nike-pegasus-40-2.jpg"
    ],
    "createdAt": "2025-09-15T10:00:00"
  }
}
```

**Test Cases:**
```bash
# Valid ID
curl http://localhost:8080/api/products/1
# ✅ Expect: Status 200, product details

# Invalid ID (không tồn tại)
curl http://localhost:8080/api/products/9999
# ✅ Expect: Status 400/404, error message
```

### 3.3. Kiểm tra Sold Calculation

**Logic:**
```
sold = total_product - SUM(all variant stocks)
```

**Test:**
1. GET product và xem `sold`, `stock`, `totalProduct`
2. GET all variants của product đó
3. Tính tổng stock của variants
4. Verify: `sold = totalProduct - sum(variant.stock)`

**Example:**
```bash
# 1. Get product
curl http://localhost:8080/api/products/5

# Response:
# "totalProduct": 250
# "stock": 180
# "sold": 70

# 2. Get variants
curl http://localhost:8080/api/product-variants/product/5

# Response: Variants with stocks [50, 40, 30, 30, 30] = 180

# 3. Verify calculation
# sold = 250 - 180 = 70 ✅ CORRECT
```

### 3.4. POST Create Product (ADMIN ONLY)

**Endpoint:**
```http
POST http://localhost:8080/api/products
Authorization: Bearer {ADMIN_TOKEN}
Content-Type: application/json
```

**Request Body (Full Example):**
```json
{
  "name": "Giày Nike React Infinity Run",
  "description": "Giày chạy bộ với công nghệ React foam, giảm chấn thương.",
  "price": 3500000,
  "discountPercent": 10,
  "brandId": 1,
  "stock": 0,
  "categoryIds": [1, 3],
  "labelIds": [1, 2],
  "images": [
    {
      "imageUrl": "uploads/react-infinity-1.jpg",
      "altText": "React Infinity Run - Front View"
    },
    {
      "imageUrl": "uploads/react-infinity-2.jpg",
      "altText": "React Infinity Run - Side View"
    }
  ],
  "variants": [
    {
      "colorId": 1,
      "sizeId": 2,
      "stock": 25,
      "price": 3500000,
      "images": [
        "uploads/react-infinity-black-s.jpg"
      ]
    },
    {
      "colorId": 1,
      "sizeId": 3,
      "stock": 30,
      "price": 3500000,
      "images": [
        "uploads/react-infinity-black-m.jpg"
      ]
    },
    {
      "colorId": 2,
      "sizeId": 2,
      "stock": 20,
      "price": 3500000,
      "images": [
        "uploads/react-infinity-white-s.jpg"
      ]
    }
  ]
}
```

**Request Body (Minimal - No Variants):**
```json
{
  "name": "Áo Thun Nike Basic",
  "description": "Áo thun cotton 100%",
  "price": 500000,
  "brandId": 1,
  "stock": 100,
  "categoryIds": [3],
  "images": [
    {
      "imageUrl": "uploads/ao-thun-nike-basic.jpg",
      "altText": "Áo Thun Nike Basic"
    }
  ]
}
```
> **Note:** Field `images` được khuyến khích có ít nhất 1 ảnh. Nếu không có ảnh, có thể dùng placeholder: `"imageUrl": "https://via.placeholder.com/500"`

**Response Success:**
```json
{
  "code": 1000,
  "message": "Tạo sản phẩm thành công",
  "result": {
    "id": 101,
    "name": "Giày Nike React Infinity Run",
    "description": "Giày chạy bộ với công nghệ React foam, giảm chấn thương.",
    "brand": {
      "id": 1,
      "name": "Nike"
    },
    "categories": [
      { "id": 1, "name": "Giày Chạy Bộ" },
      { "id": 3, "name": "Áo Thun & Áo Ba Lỗ" }
    ],
    "labels": [
      { "id": 1, "name": "Best Seller" },
      { "id": 2, "name": "New Arrival" }
    ],
    "priceInfo": {
      "price": 3500000,
      "currency": "VND",
      "costPrice": null,
      "discountPercent": 10,
      "discountPrice": 3150000
    },
    "stock": 75,
    "sold": 0,
    "totalProduct": 75,
    "thumbnail": "uploads/react-infinity-1.jpg",
    "images": [
      "uploads/react-infinity-1.jpg",
      "uploads/react-infinity-2.jpg"
    ],
    "createdAt": "2025-11-08T16:00:00"
  }
}
```

**Test với curl:**
```bash
curl -X POST http://localhost:8080/api/products ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Giày Nike Test\",\"description\":\"Test product\",\"price\":1000000,\"brandId\":1,\"stock\":50,\"categoryIds\":[1],\"images\":[{\"imageUrl\":\"uploads/test.jpg\",\"altText\":\"Test image\"}]}"
```

**Logic tự động:**
- ✅ **Auto-generate SKU** cho variants: `VAR-{productId}-1`, `VAR-{productId}-2`...
- ✅ **Auto-set thumbnail**: Ảnh đầu tiên trong `images` tự động là thumbnail
- ✅ **Auto-calculate total_product**:
  - Nếu có variants: `total_product = SUM(variant.stock)` = 75 (25+30+20)
  - Nếu không có variants: `total_product = stock` = 100
- ✅ **Auto-set stock**: `stock = SUM(variant.stock)` hoặc dùng `stock` từ request

### 3.5. PUT Update Product (ADMIN ONLY)

**Endpoint:**
```http
PUT http://localhost:8080/api/products/{id}
Authorization: Bearer {ADMIN_TOKEN}
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Giày Nike React Infinity Run 2 - Updated",
  "description": "Phiên bản cải tiến với đế giày bền hơn.",
  "price": 3800000,
  "discountPercent": 15,
  "brandId": 1,
  "stock": 0,
  "categoryIds": [1],
  "labelIds": [1],
  "images": [
    {
      "imageUrl": "uploads/react-infinity-v2-1.jpg",
      "altText": "Version 2 - Front",
      "isThumbnail": true,
      "displayOrder": 1
    },
    {
      "imageUrl": "uploads/react-infinity-v2-2.jpg",
      "altText": "Version 2 - Side",
      "isThumbnail": false,
      "displayOrder": 2
    }
  ],
  "variants": [
    {
      "sku": "REACT-INF-BLK-M",
      "colorId": 1,
      "sizeId": 3,
      "stock": 50,
      "price": 3800000,
      "images": ["uploads/react-v2-black-m.jpg"]
    },
    {
      "sku": "REACT-INF-WHT-M",
      "colorId": 2,
      "sizeId": 3,
      "stock": 40,
      "price": 3800000,
      "images": ["uploads/react-v2-white-m.jpg"]
    }
  ]
}
```

**Response:**
```json
{
  "code": 1000,
  "message": "Cập nhật sản phẩm thành công",
  "result": {
    "id": 101,
    "name": "Giày Nike React Infinity Run 2 - Updated",
    "description": "Phiên bản cải tiến với đế giày bền hơn.",
    "brand": {
      "id": 1,
      "name": "Nike"
    },
    "categories": [
      { "id": 1, "name": "Giày Chạy Bộ" }
    ],
    "labels": [
      { "id": 1, "name": "Best Seller" }
    ],
    "priceInfo": {
      "price": 3800000,
      "currency": "VND",
      "costPrice": null,
      "discountPercent": 15,
      "discountPrice": 3230000
    },
    "stock": 90,
    "sold": 0,
    "totalProduct": 90,
    "thumbnail": "uploads/react-infinity-v2-1.jpg",
    "images": [
      "uploads/react-infinity-v2-1.jpg",
      "uploads/react-infinity-v2-2.jpg"
    ],
    "createdAt": "2025-11-08T16:00:00"
  }
}
```

**Test với curl:**
```bash
curl -X PUT http://localhost:8080/api/products/101 ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Updated Product\",\"description\":\"Updated desc\",\"price\":1200000,\"brandId\":1,\"stock\":60,\"categoryIds\":[1]}"
```

**Logic update:**
- ✅ **Xóa và thay thế hoàn toàn**:
  - Categories cũ → Categories mới
  - Labels cũ → Labels mới
  - Images cũ → Images mới
  - Variants cũ → Variants mới
- ✅ **Giữ nguyên sold**:
  - Tính sold items = `old_total_product - old_total_stock`
  - Update: `new_total_product = new_total_stock + sold_items`
  - Ví dụ: Sold trước đó là 10, sau update vẫn là 10
- ✅ **Custom SKU**: Có thể tự định nghĩa SKU cho variants trong request

### 3.6. DELETE Product (ADMIN ONLY)

**Endpoint:**
```http
DELETE http://localhost:8080/api/products/{id}
Authorization: Bearer {ADMIN_TOKEN}
```

**Response:**
```json
{
  "code": 1000,
  "message": "Xóa sản phẩm thành công",
  "result": null
}
```

**Test với curl:**
```bash
curl -X DELETE http://localhost:8080/api/products/101 ^
  -H "Authorization: Bearer %TOKEN%"
```

**Logic cascade delete:**
Xóa theo thứ tự:
1. Variant images (của từng variant)
2. Variants
3. Product images
4. Product labels
5. Product categories
6. Product

**Verify deletion:**
```bash
# Try to get deleted product
curl http://localhost:8080/api/products/101
# Expected: Error 404 "Product not found"
```

---

## 4️⃣ Test Variant API (CRUD) {#test-variant-api}

### 4.1. GET Variant by ID (PUBLIC)

**Endpoint:**
```http
GET http://localhost:8080/api/product-variants/{id}
```

**Example:**
```http
GET http://localhost:8080/api/product-variants/1
```

**Response:**
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": 1,
    "productId": 1,
    "productName": "Nike Air Zoom Pegasus 40",
    "sku": "VAR-1-1",
    "stock": 25,
    "price": 3200000,
    "color": {
      "id": 1,
      "name": "Đen",
      "hexCode": "#000000"
    },
    "size": {
      "id": 1,
      "name": "S"
    },
    "images": [
      "http://localhost:8080/uploads/variant-1-1.jpg",
      "http://localhost:8080/uploads/variant-1-2.jpg"
    ],
    "createdAt": "2025-09-15T10:30:00"
  }
}
```

**Test với curl:**
```bash
curl http://localhost:8080/api/product-variants/1
```

### 4.2. GET All Variants of Product (PUBLIC)

**Endpoint:**
```http
GET http://localhost:8080/api/product-variants/product/{productId}
```

**Example:**
```http
GET http://localhost:8080/api/product-variants/product/1
```

**Response:**
```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Nike Air Zoom Pegasus 40",
      "sku": "VAR-1-1",
      "stock": 25,
      "price": 3200000,
      "color": { "id": 1, "name": "Đen", "hexCode": "#000000" },
      "size": { "id": 1, "name": "S" },
      "images": ["http://localhost:8080/uploads/variant-1.jpg"],
      "createdAt": "2025-09-15T10:30:00"
    },
    {
      "id": 2,
      "productId": 1,
      "productName": "Nike Air Zoom Pegasus 40",
      "sku": "VAR-1-2",
      "stock": 25,
      "price": 3200000,
      "color": { "id": 1, "name": "Đen", "hexCode": "#000000" },
      "size": { "id": 2, "name": "M" },
      "images": ["http://localhost:8080/uploads/variant-2.jpg"],
      "createdAt": "2025-09-15T10:35:00"
    }
  ]
}
```

**Test với curl:**
```bash
curl http://localhost:8080/api/product-variants/product/1
```

### 4.3. POST Create Variant (ADMIN ONLY)

**Endpoint:**
```http
POST http://localhost:8080/api/product-variants
Authorization: Bearer {ADMIN_TOKEN}
Content-Type: application/json
```

**Request Body:**
```json
{
  "productId": 1,
  "colorId": 2,
  "sizeId": 3,
  "stock": 50,
  "price": 3200000,
  "images": [
    "uploads/variant-new-1.jpg",
    "uploads/variant-new-2.jpg"
  ]
}
```

**Response Success:**
```json
{
  "code": 1000,
  "message": "Product variant created successfully",
  "result": {
    "id": 25,
    "productId": 1,
    "productName": "Nike Air Zoom Pegasus 40",
    "sku": "VAR-1-7",
    "stock": 50,
    "price": 3200000,
    "color": {
      "id": 2,
      "name": "Trắng",
      "hexCode": "#FFFFFF"
    },
    "size": {
      "id": 3,
      "name": "L"
    },
    "images": [
      "http://localhost:8080/uploads/variant-new-1.jpg",
      "http://localhost:8080/uploads/variant-new-2.jpg"
    ],
    "createdAt": "2025-11-08T15:30:00"
  }
}
```

**Test với Postman:**
1. Method: `POST`
2. URL: `http://localhost:8080/api/product-variants`
3. Headers:
   - `Authorization: Bearer {ADMIN_TOKEN}`
   - `Content-Type: application/json`
4. Body (raw JSON):
   ```json
   {
     "productId": 1,
     "colorId": 2,
     "sizeId": 3,
     "stock": 50,
     "price": 3200000
   }
   ```
5. Click **Send**
6. ✅ Expect: Status 201, variant created

**Test với curl:**
```bash
curl -X POST http://localhost:8080/api/product-variants ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"productId\":1,\"colorId\":2,\"sizeId\":3,\"stock\":50,\"price\":3200000}"
```

**Verify Side Effect:**
```bash
# 1. Ghi nhớ total_product trước khi create
curl http://localhost:8080/api/products/1
# Response: "totalProduct": 150

# 2. Create variant với stock=50
curl -X POST ... (như trên)

# 3. Check total_product sau khi create
curl http://localhost:8080/api/products/1
# Expected: "totalProduct": 200 (150 + 50) ✅
```

### 4.4. PUT Update Variant Stock (ADMIN ONLY)

**Endpoint:**
```http
PUT http://localhost:8080/api/product-variants/{id}/stock?stock={newStock}
Authorization: Bearer {ADMIN_TOKEN}
```

**Example:**
```http
PUT http://localhost:8080/api/product-variants/1/stock?stock=100
Authorization: Bearer {ADMIN_TOKEN}
```

**Response:**
```json
{
  "code": 1000,
  "message": "Variant stock updated successfully",
  "result": {
    "id": 1,
    "productId": 1,
    "productName": "Nike Air Zoom Pegasus 40",
    "sku": "VAR-1-1",
    "stock": 100,
    "price": 3200000,
    "color": { "id": 1, "name": "Đen", "hexCode": "#000000" },
    "size": { "id": 1, "name": "S" },
    "images": ["http://localhost:8080/uploads/variant-1.jpg"],
    "createdAt": "2025-09-15T10:30:00"
  }
}
```

**Test với curl:**
```bash
curl -X PUT "http://localhost:8080/api/product-variants/1/stock?stock=100" ^
  -H "Authorization: Bearer %TOKEN%"
```

**Verify Side Effect:**
```bash
# 1. Lấy tổng stock hiện tại
curl http://localhost:8080/api/product-variants/product/1
# Tính tổng: 25+25+25+25+25+25 = 150

# 2. Update variant 1 từ stock=25 → stock=100
curl -X PUT "http://localhost:8080/api/product-variants/1/stock?stock=100" ^
  -H "Authorization: Bearer %TOKEN%"

# 3. Lấy product và check total_product
curl http://localhost:8080/api/products/1
# Expected: totalProduct = 100+25+25+25+25+25 = 225 ✅
```

### 4.5. DELETE Variant (ADMIN ONLY)

**Endpoint:**
```http
DELETE http://localhost:8080/api/product-variants/{id}
Authorization: Bearer {ADMIN_TOKEN}
```

**Example:**
```http
DELETE http://localhost:8080/api/product-variants/25
Authorization: Bearer {ADMIN_TOKEN}
```

**Response:**
```json
{
  "code": 1000,
  "message": "Variant deleted successfully",
  "result": null
}
```

**Test với curl:**
```bash
curl -X DELETE http://localhost:8080/api/product-variants/25 ^
  -H "Authorization: Bearer %TOKEN%"
```

**Verify Side Effect:**
```bash
# 1. Lấy product trước khi xóa
curl http://localhost:8080/api/products/1
# Response: "totalProduct": 225

# 2. Xóa variant 1 (stock=100)
curl -X DELETE http://localhost:8080/api/product-variants/1 ^
  -H "Authorization: Bearer %TOKEN%"

# 3. Check total_product sau khi xóa
curl http://localhost:8080/api/products/1
# Expected: "totalProduct": 125 (225-100) ✅

# 4. Verify variant đã xóa
curl http://localhost:8080/api/product-variants/1
# Expected: Error 404 hoặc "Product not existed" ✅
```

---

## 5️⃣ Integration Test Cases {#integration-tests}

### Test Case 0: Create Product with Variants → Verify auto-calculations

**Mục tiêu:** Verify product tạo mới có total_product, stock, sold đúng

**Steps:**
```bash
# Step 1: Create product with 3 variants
curl -X POST http://localhost:8080/api/products ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test Product\",\"price\":1000000,\"brandId\":1,\"stock\":0,\"categoryIds\":[1],\"variants\":[{\"colorId\":1,\"sizeId\":2,\"stock\":30},{\"colorId\":1,\"sizeId\":3,\"stock\":40},{\"colorId\":2,\"sizeId\":2,\"stock\":50}]}"

# Response will have id: 101

# Step 2: Get created product
curl http://localhost:8080/api/products/101

# Expected Response:
# {
#   "id": 101,
#   "name": "Test Product",
#   "stock": 120,           ← sum(30+40+50)
#   "totalProduct": 120,    ← same as stock (no sales yet)
#   "sold": 0,              ← totalProduct - stock = 0
#   ...
# }

# ✅ Verify:
# - stock = 120 (30+40+50)
# - totalProduct = 120
# - sold = 0
```

### Test Case 0b: Create Product WITHOUT Variants

**Steps:**
```bash
# Create product without variants
curl -X POST http://localhost:8080/api/products ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Simple Product\",\"price\":500000,\"brandId\":1,\"stock\":100,\"categoryIds\":[3]}"

# Expected:
# {
#   "stock": 100,
#   "totalProduct": 100,
#   "sold": 0
# }

# ✅ totalProduct = stock when no variants
```

### Test Case 1: Create Variant → Verify total_product

**Mục tiêu:** Đảm bảo `total_product` tự động tăng khi tạo variant

**Steps:**
```bash
# Step 1: Get initial state
curl http://localhost:8080/api/products/5 > before.json
# Ghi nhớ: totalProduct = 250

# Step 2: Create new variant
curl -X POST http://localhost:8080/api/product-variants ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"productId\":5,\"colorId\":3,\"sizeId\":4,\"stock\":60,\"price\":1300000}"

# Step 3: Get updated state
curl http://localhost:8080/api/products/5 > after.json
# Expected: totalProduct = 310 (250+60)

# Step 4: Verify
# Compare before.json vs after.json
# ✅ totalProduct should increase by 60
```

### Test Case 2: Update Stock → Verify total_product Recalculation

**Mục tiêu:** Đảm bảo `total_product` recalculate đúng khi update stock

**Steps:**
```bash
# Step 1: Get all variants of product 5
curl http://localhost:8080/api/product-variants/product/5
# Response: [
#   {"id": 21, "stock": 50},
#   {"id": 22, "stock": 40},
#   {"id": 23, "stock": 30},
#   {"id": 24, "stock": 30},
#   {"id": 25, "stock": 30},
#   {"id": 26, "stock": 60}  <- New variant from Test Case 1
# ]
# Total: 50+40+30+30+30+60 = 240

# Step 2: Get product
curl http://localhost:8080/api/products/5
# Expected: totalProduct = 240

# Step 3: Update variant 21: stock 50 → 100
curl -X PUT "http://localhost:8080/api/product-variants/21/stock?stock=100" ^
  -H "Authorization: Bearer %TOKEN%"

# Step 4: Get product again
curl http://localhost:8080/api/products/5
# Expected: totalProduct = 290 (100+40+30+30+30+60)

# ✅ Verify: totalProduct increased by 50 (from 240 to 290)
```

### Test Case 3: Delete Variant → Verify total_product Decrease

**Mục tiêu:** Đảm bảo `total_product` giảm khi xóa variant

**Steps:**
```bash
# Step 1: Get current state
curl http://localhost:8080/api/products/5
# totalProduct = 290 (from Test Case 2)

# Step 2: Delete variant 26 (stock=60)
curl -X DELETE http://localhost:8080/api/product-variants/26 ^
  -H "Authorization: Bearer %TOKEN%"

# Step 3: Get product after deletion
curl http://localhost:8080/api/products/5
# Expected: totalProduct = 230 (290-60)

# Step 4: Verify variant is deleted
curl http://localhost:8080/api/product-variants/26
# Expected: Error "Product not existed"

# ✅ Verify: totalProduct decreased by 60
```

### Test Case 4: Update Product → Verify sold preserved

**Mục tiêu:** Khi update product, số lượng đã bán (sold) phải được giữ nguyên

**Giả định:** Product 5 hiện có:
- total_product = 250
- stock = 180 (từ variants)
- sold = 70

**Steps:**
```bash
# Step 1: Get current state
curl http://localhost:8080/api/products/5
# Response:
# "totalProduct": 250
# "stock": 180
# "sold": 70

# Step 2: Update product with new variants (stock thay đổi)
curl -X PUT http://localhost:8080/api/products/5 ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Updated Product\",\"price\":1500000,\"brandId\":1,\"stock\":0,\"categoryIds\":[1],\"variants\":[{\"sku\":\"V1\",\"colorId\":1,\"sizeId\":2,\"stock\":60},{\"sku\":\"V2\",\"colorId\":2,\"sizeId\":3,\"stock\":40}]}"

# Step 3: Get updated product
curl http://localhost:8080/api/products/5

# Expected:
# "totalProduct": 170 (100 new stock + 70 old sold)
# "stock": 100 (60+40)
# "sold": 70 ← PRESERVED!

# ✅ Verify: sold vẫn là 70 dù stock thay đổi
```

### Test Case 5: Delete Product → Verify cascade deletion

**Mục tiêu:** Xóa product phải xóa tất cả dữ liệu liên quan

**Steps:**
```bash
# Step 1: Create test product
curl -X POST http://localhost:8080/api/products ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"To Delete\",\"price\":1000000,\"brandId\":1,\"stock\":0,\"categoryIds\":[1,2],\"labelIds\":[1],\"images\":[{\"imageUrl\":\"img1.jpg\"}],\"variants\":[{\"colorId\":1,\"sizeId\":2,\"stock\":50,\"images\":[\"v-img1.jpg\"]}]}"
# Save product ID: 102

# Step 2: Verify product and related data exist
curl http://localhost:8080/api/products/102
# ✅ Product exists

curl http://localhost:8080/api/product-variants/product/102
# ✅ Has 1 variant

# Step 3: Delete product
curl -X DELETE http://localhost:8080/api/products/102 ^
  -H "Authorization: Bearer %TOKEN%"

# Step 4: Verify all deleted
curl http://localhost:8080/api/products/102
# ✅ Expected: Error "Product not found"

curl http://localhost:8080/api/product-variants/product/102
# ✅ Expected: Empty list []

# Step 5: Check database (optional)
# SELECT * FROM products WHERE id = 102;  ← 0 rows
# SELECT * FROM product_variants WHERE product_id = 102;  ← 0 rows
# SELECT * FROM product_images WHERE product_id = 102;  ← 0 rows
# SELECT * FROM product_categories WHERE product_id = 102;  ← 0 rows
# SELECT * FROM product_labels WHERE product_id = 102;  ← 0 rows
# SELECT * FROM product_variant_images WHERE product_variant_id IN (SELECT id FROM product_variants WHERE product_id = 102);  ← 0 rows
```

### Test Case 6: Duplicate variant prevention

**Mục tiêu:** Không cho phép tạo variant trùng (cùng product+color+size)

**Steps:**
```bash
# Step 1: Create variant
curl -X POST http://localhost:8080/api/product-variants ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"productId\":1,\"colorId\":1,\"sizeId\":1,\"stock\":50,\"price\":3200000}"
# Response: Success

# Step 2: Try to create same variant again
curl -X POST http://localhost:8080/api/product-variants ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"productId\":1,\"colorId\":1,\"sizeId\":1,\"stock\":100,\"price\":3500000}"
# Expected Error:
# {
#   "code": 1014,
#   "message": "Product variant already exists"
# }

# ✅ Verify: Cannot create duplicate
```

### Test Case 7: Sold Calculation Accuracy

**Mục tiêu:** Verify `sold = totalProduct - sum(variant.stock)`

**Steps:**
```bash
# Step 1: Get product
curl http://localhost:8080/api/products/1
# Response:
# "totalProduct": 150
# "stock": 100
# "sold": 50

# Step 2: Get all variants
curl http://localhost:8080/api/product-variants/product/1
# Sum all stocks: 25+25+25+25 = 100

# Step 3: Verify calculation
# sold = totalProduct - sum(variant.stock)
# sold = 150 - 100 = 50 ✅ CORRECT

# Step 4: Add new variant
curl -X POST http://localhost:8080/api/product-variants ^
  -H "Authorization: Bearer %TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"productId\":1,\"colorId\":2,\"sizeId\":3,\"stock\":30,\"price\":3200000}"

# Step 5: Get product again
curl http://localhost:8080/api/products/1
# Expected:
# "totalProduct": 180 (150+30)
# "stock": 130 (100+30)
# "sold": 50 (180-130) ✅ CORRECT
```

### Test Case 8: Authorization Check

**Mục tiêu:** Verify ADMIN-only endpoints không cho USER access

**Steps:**
```bash
# Step 1: Login as USER (not admin)
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"user@example.com\",\"password\":\"user123\"}"
# Save USER_TOKEN

# Step 2: Try to create variant with USER token
curl -X POST http://localhost:8080/api/product-variants ^
  -H "Authorization: Bearer %USER_TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"productId\":1,\"colorId\":1,\"sizeId\":1,\"stock\":50,\"price\":3200000}"
# Expected: Status 403 Forbidden

# Step 3: Try to update stock with USER token
curl -X PUT "http://localhost:8080/api/product-variants/1/stock?stock=100" ^
  -H "Authorization: Bearer %USER_TOKEN%"
# Expected: Status 403 Forbidden

# Step 4: Try to delete variant with USER token
curl -X DELETE http://localhost:8080/api/product-variants/1 ^
  -H "Authorization: Bearer %USER_TOKEN%"
# Expected: Status 403 Forbidden

# ✅ Verify: All write operations require ADMIN role
```

### Test Case 9: Product CRUD Authorization

**Mục tiêu:** Verify chỉ ADMIN mới được tạo/sửa/xóa product

**Steps:**
```bash
# Step 1: Login as USER
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"user@example.com\",\"password\":\"user123\"}"
# Save USER_TOKEN

# Step 2: Try to create product with USER token
curl -X POST http://localhost:8080/api/products ^
  -H "Authorization: Bearer %USER_TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test\",\"price\":1000000,\"brandId\":1,\"stock\":50}"
# Expected: Status 403 Forbidden

# Step 3: Try to update product with USER token
curl -X PUT http://localhost:8080/api/products/1 ^
  -H "Authorization: Bearer %USER_TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Updated\",\"price\":1200000,\"brandId\":1,\"stock\":60}"
# Expected: Status 403 Forbidden

# Step 4: Try to delete product with USER token
curl -X DELETE http://localhost:8080/api/products/1 ^
  -H "Authorization: Bearer %USER_TOKEN%"
# Expected: Status 403 Forbidden

# Step 5: Verify USER can still GET products
curl http://localhost:8080/api/products
# Expected: Status 200 Success

curl http://localhost:8080/api/products/1
# Expected: Status 200 Success

# ✅ Verify: USER can READ, but cannot CREATE/UPDATE/DELETE
```

---

## 📊 Test Summary Checklist

### Product API ✅
- [ ] GET /api/products - List all products (PUBLIC)
- [ ] GET /api/products/{id} - Get product by ID (PUBLIC)
- [ ] POST /api/products - Create product with variants (ADMIN)
- [ ] POST /api/products - Create product without variants (ADMIN)
- [ ] PUT /api/products/{id} - Update product (ADMIN)
- [ ] PUT /api/products/{id} - Update preserves sold count (ADMIN)
- [ ] DELETE /api/products/{id} - Delete product cascade (ADMIN)
- [ ] Verify `sold` calculation is correct
- [ ] Verify brand/categories/labels are objects (not strings)
- [ ] Verify auto-generated SKUs for variants on CREATE
- [ ] Verify custom SKUs on UPDATE

### Variant API ✅
- [ ] GET /api/product-variants/{id} - Get variant by ID (PUBLIC)
- [ ] GET /api/product-variants/product/{id} - Get all variants (PUBLIC)
- [ ] POST /api/product-variants - Create variant (ADMIN)
- [ ] PUT /api/product-variants/{id}/stock - Update stock (ADMIN)
- [ ] DELETE /api/product-variants/{id} - Delete variant (ADMIN)

### Integration Tests ✅
- [ ] Create product with variants → total_product = sum(variant stocks)
- [ ] Create variant → total_product increases
- [ ] Update variant stock → total_product recalculates
- [ ] Delete variant → total_product decreases
- [ ] Update product → sold count preserved
- [ ] Delete product → all related data deleted
- [ ] Duplicate variant prevention works
- [ ] Sold calculation is accurate
- [ ] Authorization enforced (ADMIN only for write operations)

---

## 🐛 Common Issues & Solutions

### Issue 1: 403 Forbidden
**Problem:** Cannot access ADMIN endpoints

**Solution:**
```bash
# Make sure you're using ADMIN token
# Re-login as admin:
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@example.com\",\"password\":\"admin123\"}"
```

### Issue 2: Brand/Category/Label not found
**Problem:** Error when creating/updating product

**Cause:** Invalid `brandId`, `categoryIds`, or `labelIds`

**Solution:**
```bash
# Get list of valid brands
curl http://localhost:8080/api/brands

# Get list of valid categories
curl http://localhost:8080/api/categories

# Get list of valid labels (if endpoint exists)
# Use IDs from these responses
```

### Issue 3: Negative sold values
**Problem:** `sold` field shows negative number

**Cause:** `total_product < sum(variant.stock)` in database

**Solution:**
```sql
-- Fix data in MySQL
UPDATE products p
SET total_product = (
    SELECT COALESCE(SUM(pv.stock), 0)
    FROM product_variants pv
    WHERE pv.product_id = p.id
);
```

### Issue 4: Duplicate variant error
**Problem:** Cannot create variant even when it's different

**Cause:** Trying to create variant with same product+color+size

**Solution:**
- Change `colorId` or `sizeId`
- Or use different `productId`

### Issue 5: Product update doesn't preserve sold
**Problem:** After updating product, `sold` changes unexpectedly

**Cause:** Bug in update logic (should be fixed in current version)

**Verify fix:**
```bash
# Before update: sold = 70
# After update with new variants: sold should still = 70
# If sold changes -> report bug
```

### Issue 6: Cannot delete product
**Problem:** Foreign key constraint error when deleting

**Cause:** Related data not deleted in correct order

**Current fix:** Service already handles cascade deletion correctly
- If still getting error, check if there are OTHER tables referencing this product
- Example: Orders, Cart items, Reviews might reference product

**Solution:**
```sql
-- Check what's referencing the product
SELECT * FROM orders WHERE id IN (SELECT order_id FROM order_items WHERE product_id = 101);
SELECT * FROM cart_items WHERE product_id = 101;
SELECT * FROM reviews WHERE product_id = 101;

-- May need to delete these first (or implement cascade in service)
```

---

## 📝 Test Data Reference

### Available Products (from init_data.sql)
- Product 1: Nike Air Zoom Pegasus 40
- Product 2: Nike Mercurial Superfly 9
- Product 5: Quần Legging Nike Pro
- Product 11: Adidas Ultraboost Light
- ... (100 products total)

### Available Colors
- 1: Đen (#000000)
- 2: Trắng (#FFFFFF)
- 3: Xám (#808080)
- 4: Xanh Navy (#000080)
- 5: Đỏ (#FF0000)

### Available Sizes
- 1: XS
- 2: S
- 3: M
- 4: L
- 5: XL
- 6: XXL

---

## 🎯 Next Steps

1. ✅ Complete all test cases trong checklist
2. ✅ Document kết quả test (Pass/Fail cho từng test case)
3. ✅ Report bugs nếu có
4. ✅ Test performance với large dataset (100+ products, 1000+ variants)
5. ✅ Test edge cases:
   - Product với 0 variants
   - Product với 50+ variants
   - Update product without changing variants
   - Delete product being referenced by orders/cart
6. ✅ Move to production testing

**Happy Testing! 🚀**
