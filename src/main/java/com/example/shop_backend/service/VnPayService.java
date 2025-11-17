package com.example.shop_backend.service;

import com.example.shop_backend.config.VnPayConfig;
import com.example.shop_backend.dto.request.PaymentRequest;
import com.example.shop_backend.model.Order;
import com.example.shop_backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnPayService {

    private final VnPayConfig config;
    private final OrderRepository orderRepository;

    public VnPayService(VnPayConfig config, OrderRepository orderRepository) {
        this.config = config;
        this.orderRepository = orderRepository;
    }

    public String createPaymentUrl(PaymentRequest request, String clientIp) throws Exception {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String orderInfo = convertToASCII("Thanh toan don hang " + order.getId());

        // Params mặc định
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", config.getTmnCode());
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_Locale", "vn");
        params.put("vnp_OrderType", "other");
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_Amount", order.getTotalAmount().multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
        params.put("vnp_ReturnUrl", config.getReturnUrl());
        params.put("vnp_IpAddr", clientIp);

        if (request.getBankCode() != null && !request.getBankCode().isBlank()) {
            params.put("vnp_BankCode", request.getBankCode());
        }

        // Thời gian tạo và hết hạn
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String createDate = formatter.format(cld.getTime());
        params.put("vnp_CreateDate", createDate);

        cld.add(Calendar.MINUTE, 15);
        String expireDate = formatter.format(cld.getTime());
        params.put("vnp_ExpireDate", expireDate);

        // Sinh txnRef duy nhất
        String txnRef = String.valueOf(System.currentTimeMillis());
        params.put("vnp_TxnRef", txnRef);

        // Sắp xếp params theo key
        Map<String, String> sortedParams = new TreeMap<>(params);

        // ===== QUAN TRỌNG: Tạo hashData VÀ query string cùng lúc =====
        // VNPay yêu cầu hashData phải dùng GIÁ TRỊ ĐÃ ENCODE
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);

            // Build hashData với giá trị đã encode
            if (hashData.length() > 0) hashData.append("&");
            hashData.append(key).append("=").append(encodedValue);

            // Build query string với giá trị đã encode
            if (query.length() > 0) query.append("&");
            query.append(key).append("=").append(encodedValue);
        }

        // Tạo chữ ký
        String secureHash = hmacSHA512(config.getHashSecret(), hashData.toString());

        // Thêm secureHash vào query
        query.append("&vnp_SecureHash=").append(secureHash);

        System.out.println("===== VNPAY HASH DATA (ENCODED) =====");
        System.out.println(hashData.toString());
        System.out.println("===== SECURE HASH =====");
        System.out.println(secureHash);
        System.out.println("===== PAYMENT URL =====");
        System.out.println(config.getVnpayUrl() + "?" + query.toString());

        return config.getVnpayUrl() + "?" + query.toString();
    }

    public boolean validateReturn(Map<String, String> params) throws Exception {
        String secureHash = params.remove("vnp_SecureHash");
        if (secureHash == null || secureHash.isEmpty()) return false;

        params.remove("vnp_SecureHashType");

        // Sắp xếp params
        Map<String, String> sorted = new TreeMap<>(params);

        // Tạo hashData - Spring đã tự động decode các giá trị từ URL
        // Nên ta cần encode lại để khớp với cách tạo ban đầu
        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            if (hashData.length() > 0) hashData.append("&");
            String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);
            hashData.append(entry.getKey()).append("=").append(encodedValue);
        }

        String calculatedHash = hmacSHA512(config.getHashSecret(), hashData.toString());

        System.out.println("===== RETURN HASH DATA (ENCODED) =====");
        System.out.println(hashData.toString());
        System.out.println("===== CALCULATED HASH =====");
        System.out.println(calculatedHash);
        System.out.println("===== VNPAY HASH =====");
        System.out.println(secureHash);

        return secureHash.equalsIgnoreCase(calculatedHash);
    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac.init(secretKey);
        byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    private String convertToASCII(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{M}", "");
        withoutDiacritics = withoutDiacritics.replaceAll("đ", "d").replaceAll("Đ", "D");
        return withoutDiacritics.replaceAll("[^\\p{ASCII}]", "");
    }
}