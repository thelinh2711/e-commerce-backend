package com.example.shop_backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponse {
    private String roomId;
    private String userName;
    private String userEmail;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
}