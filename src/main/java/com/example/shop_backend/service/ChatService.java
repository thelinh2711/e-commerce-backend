package com.example.shop_backend.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop_backend.dto.request.ChatMessageRequest;
import com.example.shop_backend.dto.response.ChatMessageResponse;
import com.example.shop_backend.dto.response.ChatRoomResponse;
import com.example.shop_backend.dto.response.InitChatResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.model.Message;
import com.example.shop_backend.model.User;
import com.example.shop_backend.model.enums.MessageType;
import com.example.shop_backend.model.enums.Role;
import com.example.shop_backend.repository.MessageRepository;
import com.example.shop_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
    @org.springframework.beans.factory.annotation.Autowired
    private com.example.shop_backend.mapper.ChatMessageMapper chatMessageMapper;

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public InitChatResponse initChat(User currentUser) {
        System.out.println("🚀 ChatService.initChat() - User: " + currentUser.getEmail());
        
        InitChatResponse.InitChatResponseBuilder responseBuilder = InitChatResponse.builder()
                .userRole(currentUser.getRole().name());

        if (currentUser.getRole() == Role.EMPLOYEE) {
            System.out.println("👨‍💼 User là EMPLOYEE - Lấy danh sách phòng");
            
            List<String> roomIds = messageRepository.findAllRoomIdsOrderByLatestMessage();
            System.out.println("📋 Tìm thấy " + roomIds.size() + " phòng chat");
            
            List<ChatRoomResponse> chatRooms = roomIds.stream()
                    .map(roomId -> {
                        try {
                            Integer userId = Integer.parseInt(roomId);
                            User user = userRepository.findById(userId).orElse(null);
                            
                            if (user == null) {
                                System.out.println("⚠️ Bỏ qua roomId " + roomId + " - User không tồn tại");
                                return null;
                            }

                            List<Message> messages = messageRepository.findLatestMessageByRoomId(roomId);
                            Message lastMessage = messages.isEmpty() ? null : messages.get(0);

                            Integer unreadCount = messageRepository.countUnreadMessagesByRoomAndUser(roomId, currentUser.getId());

                            return ChatRoomResponse.builder()
                                    .roomId(roomId)
                                    .userName(user.getFullName())
                                    .userEmail(user.getEmail())
                                    .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                                    .lastMessageTime(lastMessage != null ? lastMessage.getCreatedAt() : null)
                                    .unreadCount(unreadCount != null ? unreadCount : 0)
                                    .build();
                        } catch (NumberFormatException e) {
                            System.out.println("⚠️ RoomId không hợp lệ: " + roomId);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            responseBuilder.chatRooms(chatRooms);

        } else {
            System.out.println("👤 User là CUSTOMER - Lấy tin nhắn");
            
            String roomId = currentUser.getId().toString();
            
            // ✅ Lấy 40 tin nhắn mới nhất cho customer
            Pageable pageable = PageRequest.of(0, 40);
            List<Message> messages = messageRepository
                    .findTopNByRoomIdOrderByCreatedAtDesc(roomId, pageable);
            
            // Đảo ngược để tin nhắn cũ lên trước, mới xuống dưới
            Collections.reverse(messages);
            
            System.out.println("💬 Tìm thấy " + messages.size() + " tin nhắn");

                List<ChatMessageResponse> messageResponses = messages.stream()
                    .map(chatMessageMapper::toResponse)
                    .collect(Collectors.toList());

            responseBuilder
                    .roomId(roomId)
                    .messages(messageResponses);
        }

        return responseBuilder.build();
    }

    /**
     * ✅ SỬA: Lấy 40 tin nhắn mới nhất của room -> Cho employee xem chi tiết phòng chat
     */
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessagesByRoom(String roomId, User currentUser) {
        System.out.println("📋 ChatService.getMessagesByRoom() - Room: " + roomId);
        
        if (currentUser.getRole() != Role.EMPLOYEE) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        try {
            Integer userId = Integer.parseInt(roomId);
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            
            System.out.println("✅ User tồn tại: " + user.getEmail());
            
            // ✅ Lấy 40 tin nhắn mới nhất (DESC), sau đó đảo ngược để hiển thị đúng thứ tự
            Pageable pageable = PageRequest.of(0, 40);
            List<Message> messages = messageRepository
                    .findTopNByRoomIdOrderByCreatedAtDesc(roomId, pageable);
            
            // Đảo ngược để tin nhắn cũ lên trước, mới xuống dưới
            Collections.reverse(messages);
            
            System.out.println("💬 Tìm thấy " + messages.size() + " tin nhắn");
            
                return messages.stream()
                    .map(chatMessageMapper::toResponse)
                    .collect(Collectors.toList());
                    
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.INVALID_ORDER_REQUEST, "Room ID không hợp lệ");
        }
    }

    /**
     * ✅ MỚI: Lấy 20 tin nhắn cũ hơn (load more)
     */
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getOlderMessages(String roomId, Integer messageId, User currentUser) {
        System.out.println("📋 ChatService.getOlderMessages() - Room: " + roomId + ", Before Message ID: " + messageId);
        
        if (currentUser.getRole() != Role.EMPLOYEE && !currentUser.getId().toString().equals(roomId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        try {
            Integer userId = Integer.parseInt(roomId);
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            
            // Lấy thông tin tin nhắn làm mốc
            Message referenceMessage = messageRepository.findById(messageId)
                    .orElseThrow(() -> new AppException(ErrorCode.BAD_REQUEST, "Message không tồn tại"));
            
            System.out.println("✅ Reference message time: " + referenceMessage.getCreatedAt());
            
            // Lấy 20 tin nhắn trước thời điểm của message reference
            Pageable pageable = PageRequest.of(0, 20);
            List<Message> messages = messageRepository.findByRoomIdAndCreatedAtBefore(
                    roomId, 
                    referenceMessage.getCreatedAt(), 
                    pageable
            );
            
            // Đảo ngược để tin nhắn cũ lên trước
            Collections.reverse(messages);
            
            System.out.println("💬 Tìm thấy " + messages.size() + " tin nhắn cũ hơn");
            
                return messages.stream()
                    .map(chatMessageMapper::toResponse)
                    .collect(Collectors.toList());
                    
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.INVALID_ORDER_REQUEST, "Room ID không hợp lệ");
        }
    }

    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request, User sender) {
        System.out.println("💾 ChatService.saveMessage()");
        System.out.println("📨 Sender: " + sender);
        System.out.println("📨 Sender ID: " + (sender != null ? sender.getId() : "null"));
        System.out.println("📨 Sender Email: " + (sender != null ? sender.getEmail() : "null"));
        
        if (sender == null || sender.getId() == null) {
            System.out.println("❌ Sender null hoặc không có ID!");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        
        // Xác định người nhận
        User receiver;
        
        if (sender.getRole() == Role.EMPLOYEE) {
            System.out.println("👨‍💼 Employee gửi tin cho user trong room: " + request.getRoomId());
            receiver = userRepository.findById(Integer.parseInt(request.getRoomId()))
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            System.out.println("✅ Receiver: " + receiver.getEmail());
        } else {
            System.out.println("👤 User gửi tin, tìm employee...");
            receiver = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.EMPLOYEE)
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            System.out.println("✅ Receiver (Employee): " + receiver.getEmail());
        }

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .roomId(request.getRoomId())
                .messageType(MessageType.TEXT)
                .isRead(false)
                .build();

        System.out.println("💾 Saving message...");
        message = messageRepository.save(message);
        System.out.println("✅ Message saved with ID: " + message.getId());
        
        return chatMessageMapper.toResponse(message);
    }

    @Transactional
    public void markAsRead(String roomId, User currentUser) {
        System.out.println("👁️ ChatService.markAsRead() - Room: " + roomId);
        
        List<Message> messages = messageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
        
        int count = 0;
        for (Message m : messages) {
            if (m.getReceiver().getId().equals(currentUser.getId()) && !m.getIsRead()) {
                m.setIsRead(true);
                messageRepository.save(m);
                count++;
            }
        }
        
        System.out.println("✅ Đã đánh dấu " + count + " tin nhắn là đã đọc");
    }
}