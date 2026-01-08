package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.ChatMessage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByChatRoomId(Long chatRoomId, Pageable pageable);

    List<ChatMessage> findBySenderId(Long senderId);

    @Query("""
                SELECT m FROM ChatMessage m
                WHERE m.chatRoom.id = :chatRoomId
                AND m.isDeleted = false
                ORDER BY m.sentAt DESC
            """)
    Page<ChatMessage> findActiveByChatRoomId(
            @Param("chatRoomId") Long chatRoomId,
            Pageable pageable);

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

    @Query("""
                SELECT m FROM ChatMessage m
                WHERE m.chatRoom.id = :chatRoomId
                AND m.isPinned = true
                AND m.isDeleted = false
                ORDER BY m.pinnedAt DESC
            """)
    List<ChatMessage> findPinnedMessages(@Param("chatRoomId") Long chatRoomId);

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

    @Query("""
                SELECT m FROM ChatMessage m
                WHERE m.chatRoom.id = :chatRoomId
                AND m.isDeleted = false
                ORDER BY m.sentAt DESC
                LIMIT 1
            """)
    Optional<ChatMessage> findLastMessage(@Param("chatRoomId") Long chatRoomId);
}
