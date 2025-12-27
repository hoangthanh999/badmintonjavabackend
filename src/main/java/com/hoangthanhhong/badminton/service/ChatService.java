package com.hoangthanhhong.badminton.service;

import com.hoangthanhhong.badminton.dto.chat.ChatMessageDTO;
import com.hoangthanhhong.badminton.dto.chat.ChatRoomDTO;
import com.hoangthanhhong.badminton.dto.request.chat.CreateChatMessageRequest;
import com.hoangthanhhong.badminton.dto.request.chat.CreateChatRoomRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {

    // Chat Room operations
    ChatRoomDTO createChatRoom(CreateChatRoomRequest request, Long currentUserId);

    ChatRoomDTO getChatRoomById(Long id, Long currentUserId);

    Page<ChatRoomDTO> getUserChatRooms(Long userId, Pageable pageable);

    ChatRoomDTO getOrCreateDirectChat(Long userId1, Long userId2);

    void deleteChatRoom(Long id, Long currentUserId);

    void leaveChatRoom(Long chatRoomId, Long userId);

    // Participant operations
    void addParticipant(Long chatRoomId, Long userId, Long addedBy);

    void removeParticipant(Long chatRoomId, Long userId, Long removedBy);

    void updateParticipantRole(Long chatRoomId, Long userId, String role, Long updatedBy);

    // Message operations
    ChatMessageDTO sendMessage(CreateChatMessageRequest request, Long senderId);

    ChatMessageDTO editMessage(Long messageId, String newContent, Long userId);

    void deleteMessage(Long messageId, Long userId);

    Page<ChatMessageDTO> getChatRoomMessages(Long chatRoomId, Long userId, Pageable pageable);

    List<ChatMessageDTO> searchMessages(Long chatRoomId, String searchTerm, Long userId);

    // Message actions
    void pinMessage(Long messageId, Long userId);

    void unpinMessage(Long messageId, Long userId);

    void reactToMessage(Long messageId, String emoji, Long userId);

    void removeReaction(Long messageId, String emoji, Long userId);

    // Read receipts
    void markAsRead(Long chatRoomId, Long userId);

    void markMessageAsRead(Long messageId, Long userId);

    // Typing indicator
    void sendTypingIndicator(Long chatRoomId, Long userId, Boolean isTyping);
}
