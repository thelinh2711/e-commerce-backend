package com.example.shop_backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
    private Integer id;
    private String content;
    private String senderEmail;
    private String senderName;
    private String senderRole;
    private String roomId;
    private Boolean isRead;
    private LocalDateTime createdAt;
}