package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.ChatMessage;
import com.hoangthanhhong.badminton.enums.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByChatRoomId(Long chatRoomId, Pageable pageable);

    List<ChatMessage> findBySenderId(Long senderId);

    // === COMPLEX QUERIES ===

    // 1. Lấy tin nhắn của phòng chat (không bao gồm đã xóa)
    @Query("""
                SELECT m FROM ChatMessage m
                WHERE m.chatRoom.id = :chatRoomId
                AND m.isDeleted = false
                ORDER BY m.sentAt DESC
            """)
    Page<ChatMessage> findActiveByChatRoomId(
            @Param("chatRoomId") Long chatRoomId,
            Pageable pageable);

    // 2. Tìm kiếm tin nhắn trong phòng chat
    @Query("""
                SELECT m FROM ChatMessage m
                WHERE m.chatRoom.id = :chatRoomId
                AND m.isDeleted = false
                AND LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                ORDER BY m.sentAt DESC
            """)
    List<ChatMessage> searchInChatRoom(
            @Param("chatRoomId") Long chatRoomId,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    // 3. Lấy tin nhắn ghim
    @Query("""
                SELECT m FROM ChatMessage m
                WHERE m.chatRoom.id = :chatRoomId
                AND m.isPinned = true
                AND m.isDeleted = false
                ORDER BY m.pinnedAt DESC
            """)
    List<ChatMessage> findPinnedMessages(@Param("chatRoomId") Long chatRoomId);

    // 4. Lấy tin nhắn chưa đọc
    @Query("""
                SELECT m FROM ChatMessage m
                WHERE m.chatRoom.id = :chatRoomId
                AND m.sender.id != :userId
                AND m.sentAt > :lastReadAt
                AND m.isDeleted = false
                ORDER BY m.sentAt ASC
            """)
    List<ChatMessage> findUnreadMessages(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId,
            @Param("lastReadAt") LocalDateTime lastReadAt);

    // 5. Đếm tin nhắn chưa đọc
    @Query("""
                SELECT COUNT(m) FROM ChatMessage m
                WHERE m.chatRoom.id = :chatRoomId
                AND m.sender.id != :userId
                AND m.sentAt > :lastReadAt
                AND m.isDeleted = false
            """)
    Long countUnreadMessages(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId,
            @Param("lastReadAt") LocalDateTime lastReadAt);

    // 6. Lấy tin nhắn có media
    @Query("""
                SELECT m FROM ChatMessage m
                WHERE m.chatRoom.id = :chatRoomId
                AND m.messageType IN :mediaTypes
                AND m.isDeleted = false
                ORDER BY m.sentAt DESC
            """)
    List<ChatMessage> findMediaMessages(
            @Param("chatRoomId") Long chatRoomId,
            @Param("mediaTypes") List<MessageType> mediaTypes,
            Pageable pageable);

    // 7. Lấy tin nhắn mention user
    @Query("""
                SELECT m FROM ChatMessage m
                JOIN m.mentionedUsers u
                WHERE m.chatRoom.id = :chatRoomId
                AND u.id = :userId
                AND m.isDeleted = false
                ORDER BY m.sentAt DESC
            """)
    List<ChatMessage> findMentionedMessages(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId);

    // 8. Thống kê tin nhắn theo ngày
    @Query("""
                SELECT
                    FUNCTION('DATE', m.sentAt) as date,
                    COUNT(m) as messageCount
                FROM ChatMessage m
                WHERE m.chatRoom.id = :chatRoomId
                AND m.sentAt BETWEEN :startDate AND :endDate
                AND m.isDeleted = false
                GROUP BY FUNCTION('DATE', m.sentAt)
                ORDER BY date DESC
            """)
    List<Object[]> getMessageStatsByDate(
            @Param("chatRoomId") Long chatRoomId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 9. Lấy tin nhắn cuối cùng của phòng chat
    @Query("""
                SELECT m FROM ChatMessage m
                WHERE m.chatRoom.id = :chatRoomId
                AND m.isDeleted = false
                ORDER BY m.sentAt DESC
                LIMIT 1
            """)
    Optional<ChatMessage> findLastMessage(@Param("chatRoomId") Long chatRoomId);
}
