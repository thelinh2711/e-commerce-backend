package com.example.shop_backend.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.shop_backend.model.enums.MessageType;
import com.example.shop_backend.model.enums.SenderRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 100)
    private String senderUsername;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ADMIN', 'CLIENT') DEFAULT 'CLIENT'")
    private SenderRole senderRole = SenderRole.CLIENT;

    @Column(length = 100)
    private String senderFullName;

    @Column(length = 100)
    private String receiverUsername;

    @Column(length = 100)
    private String roomId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('TEXT', 'IMAGE', 'FILE') DEFAULT 'TEXT'")
    private MessageType messageType = MessageType.TEXT;

    private String attachmentName;

    @Column(length = 50)
    private String attachmentType;

    @Column(length = 500)
    private String attachmentUrl;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
