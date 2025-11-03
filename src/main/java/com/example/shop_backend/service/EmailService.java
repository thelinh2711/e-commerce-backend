package com.example.shop_backend.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Gmail yêu cầu header From hợp lệ
            helper.setFrom("linhkhoai1008@gmail.com", "Shop Support");
            helper.setTo(to);
            helper.setSubject(subject);

            // Nội dung HTML an toàn, tránh ký tự lỗi
            String html = "<div style='font-family: Arial, sans-serif;'>" +
                    "<h3 style='color:#007bff;'>Xin chào,</h3>" +
                    "<p>" + text + "</p>" +
                    "<p style='color:gray;font-size:12px;'>Email được gửi tự động, vui lòng không trả lời.</p>" +
                    "</div>";

            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
