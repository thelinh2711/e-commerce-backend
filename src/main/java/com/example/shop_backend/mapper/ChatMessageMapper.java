package com.example.shop_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.shop_backend.dto.response.ChatMessageResponse;
import com.example.shop_backend.model.Message;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "senderEmail", source = "sender.email")
    @Mapping(target = "senderName", source = "sender.fullName")
    @Mapping(target = "senderRole", expression = "java(message.getSender().getRole().name())")
    @Mapping(target = "roomId", source = "roomId")
    @Mapping(target = "isRead", source = "isRead")
    @Mapping(target = "createdAt", source = "createdAt")
    ChatMessageResponse toResponse(Message message);
}
