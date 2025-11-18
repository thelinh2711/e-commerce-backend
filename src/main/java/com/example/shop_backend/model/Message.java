package com.example.shop_backend.model;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import com.example.shop_backend.model.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('TEXT', 'IMAGE', 'FILE') DEFAULT 'TEXT'")
    private MessageType messageType = MessageType.TEXT;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}