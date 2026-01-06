package com.example.shop_backend.service;

import com.example.shop_backend.dto.response.RecaptchaVerifyResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RecaptchaService {

    @Value("${google.recaptcha.secret}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String VERIFY_URL =
            "https://www.google.com/recaptcha/api/siteverify";

    public void verifyCaptcha(String token) {
        if (token == null || token.isBlank()) {
            throw new AppException(ErrorCode.CAPTCHA_REQUIRED);
        }

        String url = VERIFY_URL + "?secret=" + secretKey + "&response=" + token;

        RecaptchaVerifyResponse response =
                restTemplate.postForObject(url, null, RecaptchaVerifyResponse.class);

        if (response == null || !response.isSuccess()) {
            throw new AppException(ErrorCode.INVALID_CAPTCHA);
        }
    }
}
