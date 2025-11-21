package com.example.shop_backend.dto.response;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitChatResponse {
    private String userRole;
    private String roomId;
    private List<ChatRoomResponse> chatRooms;
    private List<ChatMessageResponse> messages;
}