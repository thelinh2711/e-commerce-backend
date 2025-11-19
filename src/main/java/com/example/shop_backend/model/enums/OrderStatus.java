package com.example.shop_backend.model.enums;

public enum OrderStatus {
    PENDING,      // mới tạo
    CONFIRMED,    // đã xác nhận
    SHIPPED,      // đã giao cho đơn vị vận chuyển
    DELIVERED,    // đã giao hàng thành công
    CANCELLED     // đã hủy
}
