package com.example.shop_backend.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),

    // ✅ Lỗi xác thực
    EMAIL_ALREADY_EXISTS(1002, "Email đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
    PHONE_ALREADY_EXISTS(1003, "Số điện thoại đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1004, "Mật khẩu xác nhận không khớp", HttpStatus.BAD_REQUEST),
    CAPTCHA_REQUIRED(1005, "Vui lòng xác minh captcha", HttpStatus.BAD_REQUEST),

    // ✅ Lỗi đăng nhập
    USER_NOT_FOUND(1006, "Tài khoản không tồn tại", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(1007, "Email hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED),

    INVALID_OTP(1008, "OTP không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST),
    INVALID_RESET_TOKEN(1009, "Reset token không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_INCORRECT(1010, "Mật khẩu cũ không chính xác", HttpStatus.BAD_REQUEST),

    // Bổ sung cho phân quyền
    UNAUTHORIZED(1011, "Bạn chưa đăng nhập hoặc token không hợp lệ", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(1012, "Bạn không có quyền truy cập chức năng này", HttpStatus.FORBIDDEN),

    // ✅ Lỗi sản phẩm
    PRODUCT_NOT_FOUND(2001, "Sản phẩm không tồn tại", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_EXISTED(2001, "Sản phẩm không tồn tại", HttpStatus.NOT_FOUND),
    SLUG_EXISTED(2002, "Slug đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
    BRAND_NOT_FOUND(2003, "Thương hiệu không tồn tại", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(2004, "Danh mục không tồn tại", HttpStatus.NOT_FOUND),
    LABEL_NOT_FOUND(2005, "Nhãn không tồn tại", HttpStatus.NOT_FOUND),
    COLOR_NOT_FOUND(2006, "Màu sắc không tồn tại", HttpStatus.NOT_FOUND),
    SIZE_NOT_FOUND(2007, "Kích thước không tồn tại", HttpStatus.NOT_FOUND),
    INVALID_PRODUCT_STATUS(2008, "Trạng thái sản phẩm không hợp lệ", HttpStatus.BAD_REQUEST),
    PRODUCT_VARIANT_NOT_FOUND(2010, "Biến thể sản phẩm không tồn tại", HttpStatus.NOT_FOUND),
    PRODUCT_VARIANT_EXISTED(2011, "Biến thể sản phẩm đã tồn tại", HttpStatus.BAD_REQUEST),

    ADDRESS_NOT_FOUND(2009, "Địa chỉ không tồn tại", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_EXISTS(2010, "Tên danh mục đã tồn tại", HttpStatus.BAD_REQUEST),
    CATEGORY_HAS_PRODUCTS(2011, "Danh mục đang chứa sản phẩm, không thể xóa", HttpStatus.BAD_REQUEST),
    BRAND_EXISTED(2012, "Tên thương hiệu đã tồn tại", HttpStatus.BAD_REQUEST),
    BRAND_HAS_PRODUCTS(2013, "Không thể xóa thương hiệu vì đang có sản phẩm liên quan", HttpStatus.BAD_REQUEST),

    // Lỗi giỏ hàng
    CART_NOT_FOUND(3001, "Giỏ hàng không tồn tại", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND(3002, "Sản phẩm trong giỏ hàng không tồn tại", HttpStatus.NOT_FOUND),

    ;

    ErrorCode(int code, String message, HttpStatus httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatus httpStatusCode;
}
