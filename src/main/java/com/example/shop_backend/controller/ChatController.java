package com.example.shop_backend.controller;

import com.example.shop_backend.dto.request.ChatMessageRequest;
import com.example.shop_backend.dto.response.*;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.model.User;
import com.example.shop_backend.model.enums.Role;
import com.example.shop_backend.repository.UserRepository;
import com.example.shop_backend.security.JwtUtils;
import com.example.shop_backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @PostMapping("/init")
    public ResponseEntity<ApiResponse<InitChatResponse>> initChat(@AuthenticationPrincipal User user) {
        InitChatResponse response = chatService.initChat(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * ✅ SỬA: Lấy 40 tin nhắn mới nhất của room
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getRoomMessages(
            @PathVariable String roomId,
            @AuthenticationPrincipal User user) {
        
        List<ChatMessageResponse> messages = chatService.getMessagesByRoom(roomId, user);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    /**
     * ✅ MỚI: Load 20 tin nhắn cũ hơn (pagination)
     */
    @PostMapping("/rooms/{roomId}/messages/older")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getOlderMessages(
            @PathVariable String roomId,
            @RequestBody Map<String, Integer> request,
            @AuthenticationPrincipal User user) {
        
        Integer messageId = request.get("messageId");
        if (messageId == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "messageId là bắt buộc");
        }
        
        List<ChatMessageResponse> messages = chatService.getOlderMessages(roomId, messageId, user);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @PutMapping("/rooms/{roomId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable String roomId,
            @AuthenticationPrincipal User user) {
        
        chatService.markAsRead(roomId, user);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * WebSocket: Gửi tin nhắn
     * ✅ FIX: Load User từ token thay vì dùng @AuthenticationPrincipal
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request, StompHeaderAccessor headerAccessor) {
        System.out.println("==========================================");
        System.out.println("📨 NHẬN TIN NHẮN TỪ WEBSOCKET");
        System.out.println("📝 Content: " + request.getContent());
        System.out.println("🏠 Room ID: " + request.getRoomId());

        try {
            // ✅ LẤY TOKEN TỪ HEADER
            String token = headerAccessor.getFirstNativeHeader("Authorization");
            System.out.println("🔑 Token from header: " + (token != null ? "Present" : "NULL"));

            if (token == null || !token.startsWith("Bearer ")) {
                System.out.println("❌ Token không hợp lệ hoặc không có");
                return;
            }

            token = token.substring(7);

            // ✅ VALIDATE TOKEN
            if (!jwtUtils.validateToken(token)) {
                System.out.println("❌ Token không hợp lệ");
                return;
            }

            // ✅ LẤY EMAIL TỪ TOKEN
            String email = jwtUtils.getEmailFromToken(token);
            System.out.println("📧 Email from token: " + email);

            // ✅ LOAD USER TỪ DATABASE
            User sender = userRepository.findByEmail(email).orElse(null);

            if (sender == null) {
                System.out.println("❌ Không tìm thấy user với email: " + email);
                return;
            }

            System.out.println("✅ Sender: " + sender.getEmail());
            System.out.println("✅ Sender ID: " + sender.getId());
            System.out.println("✅ Sender Role: " + sender.getRole());

            // ✅ LƯU TIN NHẮN
            System.out.println("💾 Lưu tin nhắn...");
            ChatMessageResponse response = chatService.saveMessage(request, sender);
            System.out.println("✅ Đã lưu với ID: " + response.getId());

            // ✅ GỬI VỀ CHO NGƯỜI GỬI
            System.out.println("📤 Gửi confirmation cho sender: " + sender.getEmail());
            messagingTemplate.convertAndSendToUser(
                    sender.getEmail(),
                    "/queue/messages",
                    response
            );
            System.out.println("✅ Đã gửi confirmation");

            // ✅ GỬI CHO NGƯỜI NHẬN
            if (sender.getRole() == Role.CUSTOMER) {
                System.out.println("👤 Xử lý tin từ CUSTOMER");
                handleUserMessage(request, response);
            } else if (sender.getRole() == Role.EMPLOYEE) {
                System.out.println("👨‍💼 Xử lý tin từ EMPLOYEE");
                handleEmployeeMessage(request, response);
            }

            System.out.println("✅ Hoàn thành");

        } catch (Exception e) {
            System.out.println("❌ LỖI:");
            e.printStackTrace();
        }

        System.out.println("==========================================");
    }

    private void handleUserMessage(ChatMessageRequest request, ChatMessageResponse response) {
        List<User> employees = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.EMPLOYEE)
                .toList();

        System.out.println("📋 Tìm thấy " + employees.size() + " employee(s)");
        for (User employee : employees) {
            try {
                System.out.println("📤 Gửi đến EMPLOYEE: " + employee.getEmail());
                messagingTemplate.convertAndSendToUser(
                        employee.getEmail(),
                        "/queue/messages",
                        response
                );
                System.out.println("✅ Đã gửi thành công");
            } catch (Exception e) {
                System.out.println("❌ Lỗi gửi đến " + employee.getEmail());
            }
        }
    }

    private void handleEmployeeMessage(ChatMessageRequest request, ChatMessageResponse response) {
        try {
            Integer userId = Integer.parseInt(request.getRoomId());
            User targetUser = userRepository.findById(userId).orElse(null);

            if (targetUser != null) {
                System.out.println("✅ Tìm thấy user: " + targetUser.getEmail());
                System.out.println("📤 Gửi đến USER: " + targetUser.getEmail());

                messagingTemplate.convertAndSendToUser(
                        targetUser.getEmail(),
                        "/queue/messages",
                        response
                );

                System.out.println("✅ Đã gửi thành công");
            } else {
                System.out.println("❌ Không tìm thấy user ID: " + userId);
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ RoomId không hợp lệ: " + request.getRoomId());
        }
    }
}