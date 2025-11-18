package com.example.shop_backend.repository;

import com.example.shop_backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    
    // Lấy tất cả tin nhắn trong phòng, sắp xếp theo thời gian tăng dần
    List<Message> findByRoomIdOrderByCreatedAtAsc(String roomId);
    
    // Lấy tin nhắn mới nhất của phòng
    @Query("SELECT m FROM Message m WHERE m.roomId = :roomId ORDER BY m.createdAt DESC")
    List<Message> findLatestMessageByRoomId(@Param("roomId") String roomId);
    
    // Lấy danh sách roomId, sắp xếp theo tin nhắn mới nhất
    @Query("SELECT DISTINCT m.roomId FROM Message m GROUP BY m.roomId ORDER BY MAX(m.createdAt) DESC")
    List<String> findAllRoomIdsOrderByLatestMessage();
    
    // Đếm số tin nhắn chưa đọc trong phòng của 1 user
    @Query("SELECT COUNT(m) FROM Message m WHERE m.roomId = :roomId AND m.receiver.id = :userId AND m.isRead = false")
    Integer countUnreadMessagesByRoomAndUser(@Param("roomId") String roomId, @Param("userId") Integer userId);
}