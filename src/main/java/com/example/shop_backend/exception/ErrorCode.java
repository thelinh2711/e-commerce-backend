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

    // ✅ Lỗi sản phẩm
    PRODUCT_NOT_FOUND(2001, "Sản phẩm không tồn tại", HttpStatus.NOT_FOUND),
    SLUG_EXISTED(2002, "Slug đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
    BRAND_NOT_FOUND(2003, "Thương hiệu không tồn tại", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(2004, "Danh mục không tồn tại", HttpStatus.NOT_FOUND),
    LABEL_NOT_FOUND(2005, "Nhãn không tồn tại", HttpStatus.NOT_FOUND),
    COLOR_NOT_FOUND(2006, "Màu sắc không tồn tại", HttpStatus.NOT_FOUND),
    SIZE_NOT_FOUND(2007, "Kích thước không tồn tại", HttpStatus.NOT_FOUND),
    INVALID_PRODUCT_STATUS(2008, "Trạng thái sản phẩm không hợp lệ", HttpStatus.BAD_REQUEST),

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
