# Hướng dẫn API Voucher

## 📋 Mục lục
1. [Tổng quan](#tổng-quan)
2. [Model & Enum](#model--enum)
3. [API Endpoints](#api-endpoints)
4. [Logic nghiệp vụ](#logic-nghiệp-vụ)
5. [Ví dụ sử dụng](#ví-dụ-sử-dụng)

---

## 🎯 Tổng quan

Hệ thống Voucher hỗ trợ 3 loại mã giảm giá:
- **PERCENTAGE**: Giảm theo phần trăm (%, có giới hạn tối đa)
- **FIXED_AMOUNT**: Giảm theo số tiền cố định
- **FREESHIP**: Miễn phí vận chuyển

### Các trạng thái Voucher
- **ACTIVE**: Đang hoạt động, có thể sử dụng
- **INACTIVE**: Tạm ngừng, không thể sử dụng
- **EXPIRED**: Đã hết hạn
- **OUT_OF_STOCK**: Hết lượt sử dụng

---

## 📊 Model & Enum

### Voucher Model
```java
{
  "id": Integer,                    // ID tự động
  "code": String,                   // Mã voucher (unique, bắt buộc)
  "discountType": VoucherDiscountType, // Loại giảm giá
  "discountValue": BigDecimal,      // Giá trị giảm
  "maxDiscountValue": BigDecimal,   // Giới hạn giảm tối đa
  "minOrderValue": BigDecimal,      // Giá trị đơn hàng tối thiểu
  "usageLimit": Integer,            // Giới hạn số lần sử dụng
  "usageCount": Integer,            // Số lần đã sử dụng
  "startDate": LocalDateTime,       // Ngày bắt đầu
  "endDate": LocalDateTime,         // Ngày kết thúc
  "status": StatusVoucher,          // Trạng thái
  "description": String,            // Mô tả
  "createdAt": LocalDateTime        // Ngày tạo
}
```

### VoucherDiscountType
```java
PERCENTAGE     // Giảm theo %
FIXED_AMOUNT   // Giảm theo số tiền
FREESHIP       // Miễn phí ship
```

### StatusVoucher
```java
ACTIVE         // Đang hoạt động
INACTIVE       // Tạm ngừng
EXPIRED        // Hết hạn
OUT_OF_STOCK   // Hết lượt
```

---

## 🔌 API Endpoints

### 1. **POST /api/vouchers** - Tạo voucher mới

**Request Body:**
```json
{
  "code": "GIAM20",                  // ✅ BẮT BUỘC, unique
  "discountType": "PERCENTAGE",      // ✅ BẮT BUỘC (PERCENTAGE | FIXED_AMOUNT | FREESHIP)
  "discountValue": 20,               // ✅ BẮT BUỘC, >= 0
  "maxDiscountValue": 50000,         // ⭕ OPTIONAL
  "minOrderValue": 200000,           // ⭕ OPTIONAL
  "usageLimit": 100,                 // ✅ BẮT BUỘC, >= 1
  "startDate": "2025-11-14T00:00:00", // ✅ BẮT BUỘC
  "endDate": "2025-12-31T23:59:59",  // ✅ BẮT BUỘC
  "description": "Giảm 20% tối đa 50k" // ⭕ OPTIONAL
}
```

**Trường bắt buộc:**
- ✅ `code` - Mã voucher (unique, không trùng)
- ✅ `discountType` - Loại voucher (PERCENTAGE | FIXED_AMOUNT | FREESHIP)
- ✅ `discountValue` - Giá trị giảm (>= 0)
- ✅ `usageLimit` - Giới hạn số lần dùng (>= 1)
- ✅ `startDate` - Ngày bắt đầu
- ✅ `endDate` - Ngày kết thúc (phải sau startDate)

**Trường optional:**
- ⭕ `maxDiscountValue` - Giới hạn giảm tối đa
- ⭕ `minOrderValue` - Giá trị đơn hàng tối thiểu
- ⭕ `description` - Mô tả voucher

**Response:**
```json
{
  "code": 1000,
  "result": {
    "id": 1,
    "code": "GIAM20",
    "discountType": "PERCENTAGE",
    "discountValue": 20,
    "maxDiscountValue": 50000,
    "minOrderValue": 200000,
    "usageLimit": 100,
    "usageCount": 0,
    "startDate": "2025-11-14T00:00:00",
    "endDate": "2025-12-31T23:59:59",
    "status": "ACTIVE",
    "description": "Giảm 20% tối đa 50k",
    "createdAt": "2025-11-14T10:30:00",
    "isActive": true,
    "isExpired": false,
    "remainingUses": 100
  }
}
```

**Validation:**
- ✅ Code phải unique
- ✅ endDate phải sau startDate
- ✅ discountValue >= 0
- ✅ usageLimit >= 1

---

### 2. **PUT /api/vouchers/{id}** - Cập nhật voucher

**Request Body (Partial Update - TẤT CẢ TRƯỜNG ĐỀU OPTIONAL):**
```json
{
  "code": "NEWCODE",                 // ⭕ OPTIONAL (phải unique nếu thay đổi)
  "discountType": "FIXED_AMOUNT",    // ⭕ OPTIONAL
  "discountValue": 30,               // ⭕ OPTIONAL (>= 0)
  "maxDiscountValue": 100000,        // ⭕ OPTIONAL
  "minOrderValue": 250000,           // ⭕ OPTIONAL
  "usageLimit": 200,                 // ⭕ OPTIONAL (>= 1)
  "startDate": "2025-11-15T00:00:00", // ⭕ OPTIONAL
  "endDate": "2025-12-20T23:59:59",  // ⭕ OPTIONAL
  "status": "INACTIVE",              // ⭕ OPTIONAL (ACTIVE | INACTIVE | EXPIRED | OUT_OF_STOCK)
  "description": "Updated"           // ⭕ OPTIONAL
}
```

**Tất cả trường đều OPTIONAL:**
- ⭕ Chỉ update các trường có trong request
- ⭕ Các trường không gửi sẽ giữ nguyên giá trị cũ
- ⭕ `code` - Phải unique nếu thay đổi
- ⭕ `discountValue` - Phải >= 0 nếu có
- ⭕ `usageLimit` - Phải >= 1 nếu có
- ⭕ Validate: endDate >= startDate (sau khi merge giá trị mới/cũ)

**Không thể update:**
- ❌ `id` - ID tự động
- ❌ `usageCount` - Chỉ tăng khi sử dụng
- ❌ `createdAt` - Ngày tạo

**Response:** Giống POST

---

### 3. **DELETE /api/vouchers/{id}** - Xóa voucher

**Response:**
```json
{
  "code": 1000,
  "message": "Xóa voucher thành công"
}
```

---

### 4. **GET /api/vouchers/{id}** - Lấy voucher theo ID

**Response:** Giống POST

---

### 5. **GET /api/vouchers/code/{code}** - Lấy voucher theo mã

**Example:** `GET /api/vouchers/code/GIAM20`

**Response:** Giống POST

---

### 6. **GET /api/vouchers** - Lấy danh sách vouchers

**Query Parameters:**
- `status` (optional): Lọc theo trạng thái (ACTIVE, INACTIVE, EXPIRED, OUT_OF_STOCK)

**Examples:**
- `GET /api/vouchers` - Lấy tất cả
- `GET /api/vouchers?status=ACTIVE` - Chỉ lấy active

**Response:**
```json
{
  "code": 1000,
  "result": [
    { /* voucher 1 */ },
    { /* voucher 2 */ }
  ]
}
```

---

### 7. **GET /api/vouchers/active** - Lấy vouchers đang hoạt động

**Logic:**
- status = ACTIVE
- startDate <= now
- endDate >= now
- usageCount < usageLimit

**Response:** Danh sách vouchers

---

### 8. **GET /api/vouchers/validate/{code}** - Kiểm tra voucher có hợp lệ

**Example:** `GET /api/vouchers/validate/GIAM20`

**Logic kiểm tra:**
1. Voucher có tồn tại không?
2. Status = ACTIVE?
3. Thời gian hợp lệ (startDate <= now <= endDate)?
4. Còn lượt sử dụng (usageCount < usageLimit)?

**Response (Success):**
```json
{
  "code": 1000,
  "message": "Voucher hợp lệ",
  "result": { /* voucher details */ }
}
```

**Response (Error):**
```json
{
  "code": 4003,
  "message": "Voucher đã hết hạn"
}
```

**Error Codes:**
- 4001: Voucher không tồn tại
- 4003: Voucher đã hết hạn
- 4004: Voucher chưa được kích hoạt
- 4005: Voucher đã hết lượt sử dụng

---

### 9. **PUT /api/vouchers/update-expired** - Cập nhật vouchers hết hạn

**Logic:**
- Tìm tất cả vouchers có endDate < now và status != EXPIRED
- Set status = EXPIRED

**Response:**
```json
{
  "code": 1000,
  "message": "Cập nhật vouchers hết hạn thành công"
}
```

**Note:** API này có thể được gọi bởi scheduler tự động mỗi ngày

---

## 💡 Logic nghiệp vụ

### 1. Logic 3 loại Voucher

#### **PERCENTAGE - Giảm theo phần trăm**
```json
{
  "code": "GIAM20",
  "discountType": "PERCENTAGE",
  "discountValue": 20,           // 20%
  "maxDiscountValue": 50000,     // Giảm tối đa 50k
  "minOrderValue": 200000        // Đơn tối thiểu 200k
}
```

**Cách tính:**
```javascript
if (orderTotal >= minOrderValue) {
  discount = Math.min(
    orderTotal * (discountValue / 100),
    maxDiscountValue
  );
}

// Ví dụ: Đơn 500k, giảm 20% = 100k → Áp dụng max 50k
// Kết quả: Giảm 50k
```

#### **FIXED_AMOUNT - Giảm số tiền cố định**
```json
{
  "code": "GIAM50K",
  "discountType": "FIXED_AMOUNT",
  "discountValue": 50000,        // Giảm 50k
  "maxDiscountValue": null,      // Không cần
  "minOrderValue": 300000        // Đơn tối thiểu 300k
}
```

**Cách tính:**
```javascript
if (orderTotal >= minOrderValue) {
  discount = discountValue; // 50000
}

// Đơn 500k → Giảm 50k
// Đơn 300k → Giảm 50k
// Đơn 299k → Không được dùng
```

#### **FREESHIP - Miễn phí ship**
```json
{
  "code": "FREESHIP",
  "discountType": "FREESHIP",
  "discountValue": null,         // Không cần
  "maxDiscountValue": 30000,     // Ship tối đa 30k
  "minOrderValue": 0             // Không giới hạn
}
```

**Cách tính:**
```javascript
if (orderTotal >= minOrderValue) {
  discount = Math.min(shippingFee, maxDiscountValue);
}

// Phí ship 25k → Giảm 25k
// Phí ship 50k → Giảm 30k (max)
```

---

### 2. Calculated Fields trong Response

#### **isActive** - Voucher có đang hoạt động?
```java
isActive = status == ACTIVE 
        && startDate <= now 
        && endDate >= now 
        && usageCount < usageLimit
```

#### **isExpired** - Voucher đã hết hạn?
```java
isExpired = endDate < now || status == EXPIRED
```

#### **remainingUses** - Số lượt còn lại
```java
remainingUses = Math.max(0, usageLimit - usageCount)
```

---

### 3. Flow sử dụng Voucher

```
1. Customer nhập mã voucher
   ↓
2. Frontend gọi GET /api/vouchers/validate/{code}
   ↓
3. Backend kiểm tra:
   - Voucher tồn tại?
   - Status = ACTIVE?
   - Trong thời gian hiệu lực?
   - Còn lượt sử dụng?
   ↓
4. Nếu hợp lệ → Tính discount theo loại voucher
   ↓
5. Apply discount vào đơn hàng
   ↓
6. Khi đặt hàng thành công:
   - usageCount++
   - Nếu usageCount >= usageLimit → status = OUT_OF_STOCK
```

---

## 📝 Ví dụ sử dụng

### Ví dụ 1: Tạo voucher giảm 20%

**Request:**
```bash
POST /api/vouchers
Content-Type: application/json

{
  "code": "BLACK20",
  "discountType": "PERCENTAGE",
  "discountValue": 20,
  "maxDiscountValue": 100000,
  "minOrderValue": 500000,
  "usageLimit": 1000,
  "startDate": "2025-11-20T00:00:00",
  "endDate": "2025-11-30T23:59:59",
  "description": "Black Friday - Giảm 20% tối đa 100k cho đơn từ 500k"
}
```

**Use case:**
- Đơn 600k → Giảm 120k → Áp dụng max 100k
- Đơn 1,000k → Giảm 200k → Áp dụng max 100k
- Đơn 400k → Không đủ điều kiện

---

### Ví dụ 2: Tạo voucher giảm giá cố định

**Request:**
```bash
POST /api/vouchers

{
  "code": "WELCOME50K",
  "discountType": "FIXED_AMOUNT",
  "discountValue": 50000,
  "minOrderValue": 200000,
  "usageLimit": 500,
  "startDate": "2025-11-14T00:00:00",
  "endDate": "2025-12-31T23:59:59",
  "description": "Tặng 50k cho khách hàng mới"
}
```

**Use case:**
- Đơn 200k → Giảm 50k → Thanh toán 150k
- Đơn 500k → Giảm 50k → Thanh toán 450k
- Đơn 190k → Không đủ điều kiện

---

### Ví dụ 3: Tạo voucher freeship

**Request:**
```bash
POST /api/vouchers

{
  "code": "FREESHIP30K",
  "discountType": "FREESHIP",
  "maxDiscountValue": 30000,
  "minOrderValue": 0,
  "usageLimit": 10000,
  "startDate": "2025-11-14T00:00:00",
  "endDate": "2025-11-30T23:59:59",
  "description": "Miễn phí ship tối đa 30k"
}
```

**Use case:**
- Phí ship 20k → Giảm 20k → Ship miễn phí
- Phí ship 50k → Giảm 30k → Còn 20k phí ship
- Đơn bất kỳ → Đều áp dụng được

---

### Ví dụ 4: Update voucher

**Request:**
```bash
PUT /api/vouchers/1

{
  "usageLimit": 2000,
  "endDate": "2025-12-15T23:59:59",
  "status": "ACTIVE"
}
```

**Kết quả:** Chỉ 3 trường này được update, các trường khác giữ nguyên

---

### Ví dụ 5: Validate voucher trước khi apply

**Request:**
```bash
GET /api/vouchers/validate/GIAM20
```

**Response (Hợp lệ):**
```json
{
  "code": 1000,
  "message": "Voucher hợp lệ",
  "result": {
    "id": 1,
    "code": "GIAM20",
    "discountType": "PERCENTAGE",
    "discountValue": 20,
    "maxDiscountValue": 50000,
    "isActive": true,
    "isExpired": false,
    "remainingUses": 85
  }
}
```

**Response (Hết hạn):**
```json
{
  "code": 4003,
  "message": "Voucher đã hết hạn"
}
```

---

## 🔐 Error Codes

| Code | Message | Mô tả |
|------|---------|-------|
| 4001 | Voucher không tồn tại | Không tìm thấy voucher với ID/code |
| 4002 | Mã voucher đã tồn tại | Code bị trùng khi tạo/update |
| 4003 | Voucher đã hết hạn | endDate < now hoặc status = EXPIRED |
| 4004 | Voucher chưa được kích hoạt | status != ACTIVE |
| 4005 | Voucher đã hết lượt sử dụng | usageCount >= usageLimit |
| 4006 | Ngày kết thúc phải sau ngày bắt đầu | Validation lỗi |

---

## 🎯 Best Practices

### 1. Khi tạo voucher
- ✅ Code nên ngắn gọn, dễ nhớ (VD: GIAM20, FREESHIP)
- ✅ Set usageLimit hợp lý tránh lạm dụng
- ✅ Set minOrderValue để tránh lỗ
- ✅ Với PERCENTAGE, bắt buộc set maxDiscountValue

### 2. Khi validate voucher
- ✅ Luôn gọi `/validate/{code}` trước khi apply
- ✅ Hiển thị rõ lý do nếu voucher không hợp lệ
- ✅ Kiểm tra minOrderValue phía frontend trước

### 3. Quản lý voucher
- ✅ Định kỳ gọi `/update-expired` để cập nhật status
- ✅ Monitor usageCount để biết voucher hot
- ✅ Set status = INACTIVE khi muốn tạm dừng (không cần xóa)

---

## 🔄 Workflow tích hợp vào Checkout

```javascript
// 1. User nhập mã voucher
const voucherCode = "GIAM20";

// 2. Validate voucher
const validateResponse = await fetch(`/api/vouchers/validate/${voucherCode}`);
if (!validateResponse.ok) {
  // Hiển thị lỗi: "Voucher không hợp lệ"
  return;
}

const voucher = validateResponse.data.result;

// 3. Kiểm tra minOrderValue
if (orderTotal < voucher.minOrderValue) {
  alert(`Đơn hàng tối thiểu ${voucher.minOrderValue}đ`);
  return;
}

// 4. Tính discount
let discount = 0;
switch(voucher.discountType) {
  case 'PERCENTAGE':
    discount = Math.min(
      orderTotal * voucher.discountValue / 100,
      voucher.maxDiscountValue
    );
    break;
  case 'FIXED_AMOUNT':
    discount = voucher.discountValue;
    break;
  case 'FREESHIP':
    discount = Math.min(shippingFee, voucher.maxDiscountValue);
    break;
}

// 5. Apply discount
finalTotal = orderTotal - discount;

// 6. Khi đặt hàng thành công
// Backend tự động: voucher.usageCount++
```

---

## 📌 Notes

- Voucher chỉ áp dụng được 1 lần cho mỗi đơn hàng
- Admin có thể tạo voucher với usageLimit = 1 cho voucher cá nhân
- FREESHIP có thể kết hợp với voucher giảm giá khác (tùy logic)
- Status AUTO update: usageCount >= usageLimit → OUT_OF_STOCK
