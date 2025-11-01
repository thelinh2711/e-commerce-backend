package com.example.shop_backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

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
