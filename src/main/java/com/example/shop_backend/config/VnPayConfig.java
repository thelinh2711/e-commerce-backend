package com.example.shop_backend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class VnPayConfig {

    @Getter
    @Value("${payment.vnPay.url}")
    private String vnpayUrl;

    @Getter
    @Value("${payment.vnPay.returnUrl}")
    private String returnUrl;

    @Getter
    @Value("${payment.vnPay.tmnCode}")
    private String tmnCode;

    @Getter
    @Value("${payment.vnPay.hashSecret}")
    private String hashSecret;

    @Value("${payment.vnPay.version}")
    private String version;

    @Value("${payment.vnPay.command}")
    private String command;

    @Value("${payment.vnPay.orderType}")
    private String orderType;

    public Map<String, String> getDefaultParams() {
        Map<String, String> map = new HashMap<>();
        map.put("vnp_Version", version);
        map.put("vnp_Command", command);
        map.put("vnp_TmnCode", tmnCode);
        map.put("vnp_CurrCode", "VND");
        map.put("vnp_OrderType", orderType);
        map.put("vnp_Locale", "vn");
        map.put("vnp_ReturnUrl", returnUrl);
        return map;
    }
}
