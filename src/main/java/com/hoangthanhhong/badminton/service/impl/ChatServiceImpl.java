package com.hoangthanhhong.badminton.service.impl;

import com.hoangthanhhong.badminton.dto.chat.ChatMessageDTO;
import com.hoangthanhhong.badminton.dto.chat.ChatRoomDTO;
import com.hoangthanhhong.badminton.dto.chat.TypingIndicatorDTO;
import com.hoangthanhhong.badminton.dto.request.chat.CreateChatMessageRequest;
import com.hoangthanhhong.badminton.dto.request.chat.CreateChatRoomRequest;
import com.hoangthanhhong.badminton.entity.*;
import com.hoangthanhhong.badminton.enums.ChatRole;
import com.hoangthanhhong.badminton.enums.ChatRoomType;
import com.hoangthanhhong.badminton.exception.BadRequestException;
import com.hoangthanhhong.badminton.exception.ForbiddenException;
import com.hoangthanhhong.badminton.exception.ResourceNotFoundException;
import com.hoangthanhhong.badminton.mapper.ChatMessageMapper;
import com.hoangthanhhong.badminton.mapper.ChatRoomMapper;
import com.hoangthanhhong.badminton.repository.*;
import com.hoangthanhhong.badminton.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository participantRepository;
    private final ChatMessageRepository messageRepository;
    private final MessageReactionRepository reactionRepository;
    private final MessageReadReceiptRepository readReceiptRepository;
    private final UserRepository userRepository;
    private final ChatRoomMapper chatRoomMapper;
    private final ChatMessageMapper messageMapper;
    private final SimpMessagingTemplate messagingTemplate;

    // ===== CHAT ROOM OPERATIONS =====

    @Override
    public ChatRoomDTO createChatRoom(CreateChatRoomRequest request, Long currentUserId) {
        User creator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ChatRoom chatRoom = ChatRoom.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .avatar(request.getAvatar())
                .maxMembers(request.getMaxMembers() != null ? request.getMaxMembers() : 100)
                .isPrivate(request.getIsPrivate() != null ? request.getIsPrivate() : false)
                .createdByUserId(currentUserId)
                .status("ACTIVE")
                .build();

        chatRoom = chatRoomRepository.save(chatRoom);

        // Add creator as owner
        ChatParticipant ownerParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(creator)
                .role(ChatRole.OWNER)
                .status("ACTIVE")
                .joinedAt(LocalDateTime.now())
                .build();
        participantRepository.save(ownerParticipant);

        // Add other participants
        if (request.getParticipantUserIds() != null && !request.getParticipantUserIds().isEmpty()) {
            for (Long userId : request.getParticipantUserIds()) {
                if (!userId.equals(currentUserId)) {
                    addParticipant(chatRoom.getId(), userId, currentUserId);
                }
            }
        }

        log.info("Created chat room: {} by user: {}", chatRoom.getId(), currentUserId);
        return chatRoomMapper.toDTOWithDetails(chatRoom, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatRoomDTO getChatRoomById(Long id, Long currentUserId) {
        ChatRoom chatRoom = chatRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));

        // Check if user is participant
        if (!chatRoom.isUserParticipant(currentUserId)) {
            throw new ForbiddenException("You are not a participant of this chat room");
        }

        return chatRoomMapper.toDTOWithDetails(chatRoom, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatRoomDTO> getUserChatRooms(Long userId, Pageable pageable) {
        Page<ChatRoom> chatRooms = chatRoomRepository.findUserChatRooms(userId, pageable);
        return chatRooms.map(room -> chatRoomMapper.toDTOWithDetails(room, userId));
    }

    @Override
    public ChatRoomDTO getOrCreateDirectChat(Long userId1, Long userId2) {
        // Check if direct chat already exists
        return chatRoomRepository.findDirectChatBetweenUsers(userId1, userId2)
                .map(room -> chatRoomMapper.toDTOWithDetails(room, userId1))
                .orElseGet(() -> {
                    // Create new direct chat
                    User user1 = userRepository.findById(userId1)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    User user2 = userRepository.findById(userId2)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                    ChatRoom chatRoom = ChatRoom.builder()
                            .name(user1.getName() + " & " + user2.getName())
                            .type(ChatRoomType.DIRECT)
                            .maxMembers(2)
                            .isPrivate(true)
                            .createdByUserId(userId1)
                            .status("ACTIVE")
                            .build();

                    chatRoom = chatRoomRepository.save(chatRoom);

                    // Add both users as participants
                    ChatParticipant participant1 = ChatParticipant.builder()
                            .chatRoom(chatRoom)
                            .user(user1)
                            .role(ChatRole.MEMBER)
                            .status("ACTIVE")
                            .joinedAt(LocalDateTime.now())
                            .build();

                    ChatParticipant participant2 = ChatParticipant.builder()
                            .chatRoom(chatRoom)
                            .user(user2)
                            .role(ChatRole.MEMBER)
                            .status("ACTIVE")
                            .joinedAt(LocalDateTime.now())
                            .build();

                    participantRepository.save(participant1);
                    participantRepository.save(participant2);

                    log.info("Created direct chat between users: {} and {}", userId1, userId2);
                    return chatRoomMapper.toDTOWithDetails(chatRoom, userId1);
                });
    }

    @Override
    public void deleteChatRoom(Long id, Long currentUserId) {
        ChatRoom chatRoom = chatRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));

        // Check if user is owner
        ChatParticipant participant = participantRepository.findByChatRoomIdAndUserId(id, currentUserId)
                .orElseThrow(() -> new ForbiddenException("You are not a participant"));

        if (participant.getRole() != ChatRole.OWNER) {
            throw new ForbiddenException("Only owner can delete chat room");
        }

        chatRoom.softDelete();
        chatRoomRepository.save(chatRoom);

        log.info("Deleted chat room: {} by user: {}", id, currentUserId);
    }

    @Override
    public void leaveChatRoom(Long chatRoomId, Long userId) {
        ChatParticipant participant = participantRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        participant.leave();
        participantRepository.save(participant);

        // Send system message
        sendSystemMessage(chatRoomId, participant.getUser().getName() + " left the chat");

        log.info("User {} left chat room {}", userId, chatRoomId);
    }

    // ===== PARTICIPANT OPERATIONS =====

    @Override
    public void addParticipant(Long chatRoomId, Long userId, Long addedBy) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if already participant
        if (participantRepository.existsActiveByChatRoomIdAndUserId(chatRoomId, userId)) {
            throw new BadRequestException("User is already a participant");
        }

        // Check if room is full
        if (chatRoom.isFull()) {
            throw new BadRequestException("Chat room is full");
        }

        ChatParticipant participant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(user)
                .role(ChatRole.MEMBER)
                .status("ACTIVE")
                .joinedAt(LocalDateTime.now())
                .addedByUserId(addedBy)
                .build();

        participantRepository.save(participant);

        // Send system message
        sendSystemMessage(chatRoomId, user.getName() + " joined the chat");

        // Notify new participant
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/chat-room-added",
                chatRoomMapper.toDTOWithDetails(chatRoom, userId));

        log.info("Added user {} to chat room {} by user {}", userId, chatRoomId, addedBy);
    }

    @Override
    public void removeParticipant(Long chatRoomId, Long userId, Long removedBy) {
        ChatParticipant remover = participantRepository.findByChatRoomIdAndUserId(chatRoomId, removedBy)
                .orElseThrow(() -> new ForbiddenException("You are not a participant"));

        if (!remover.isAdmin()) {
            throw new ForbiddenException("Only admins can remove participants");
        }

        ChatParticipant participant = participantRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        participant.kick(removedBy, "Removed by admin");
        participantRepository.save(participant);

        // Send system message
        sendSystemMessage(chatRoomId, participant.getUser().getName() + " was removed from the chat");

        log.info("Removed user {} from chat room {} by user {}", userId, chatRoomId, removedBy);
    }

    @Override
    public void updateParticipantRole(Long chatRoomId, Long userId, String role, Long updatedBy) {
        ChatParticipant updater = participantRepository.findByChatRoomIdAndUserId(chatRoomId, updatedBy)
                .orElseThrow(() -> new ForbiddenException("You are not a participant"));

        if (updater.getRole() != ChatRole.OWNER) {
            throw new ForbiddenException("Only owner can change roles");
        }

        ChatParticipant participant = participantRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        if ("ADMIN".equals(role)) {
            participant.promoteToAdmin();
        } else if ("MEMBER".equals(role)) {
            participant.demoteToMember();
        }

        participantRepository.save(participant);

        log.info("Updated role of user {} in chat room {} to {} by user {}",
                userId, chatRoomId, role, updatedBy);
    }

    // ===== MESSAGE OPERATIONS =====

    @Override
    public ChatMessageDTO sendMessage(CreateChatMessageRequest request, Long senderId) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user is participant and can send message
        ChatParticipant participant = participantRepository
                .findByChatRoomIdAndUserId(request.getChatRoomId(), senderId)
                .orElseThrow(() -> new ForbiddenException("You are not a participant"));

        if (!participant.canSendMessage()) {
            throw new ForbiddenException("You cannot send messages in this chat room");
        }

        // Create message
        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .messageType(request.getMessageType())
                .content(request.getContent())
                .fileUrl(request.getFileUrl())
                .fileName(request.getFileName())
                .fileSize(request.getFileSize())
                .fileType(request.getFileType())
                .thumbnailUrl(request.getThumbnailUrl())
                .duration(request.getDuration())
                .isForwarded(request.getIsForwarded() != null ? request.getIsForwarded() : false)
                .originalMessageId(request.getOriginalMessageId())
                .metadata(request.getMetadata())
                .build();

        // Handle reply
        if (request.getParentMessageId() != null) {
            ChatMessage parentMessage = messageRepository.findById(request.getParentMessageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent message not found"));
            message.setParentMessage(parentMessage);
        }

        // Handle mentions
        if (request.getMentionedUserIds() != null && !request.getMentionedUserIds().isEmpty()) {
            List<User> mentionedUsers = userRepository.findAllById(request.getMentionedUserIds());
            message.setMentionedUsers(mentionedUsers);
        }

        message = messageRepository.save(message);

        // Update chat room
        chatRoom.addMessage(message);
        chatRoomRepository.save(chatRoom);

        // Update unread count for other participants
        updateUnreadCounts(chatRoom.getId(), senderId);

        // Update participant's last seen
        participant.updateLastSeen();
        participantRepository.save(participant);

        // Send message via WebSocket
        ChatMessageDTO messageDTO = messageMapper.toDTOWithParent(message);
        messagingTemplate.convertAndSend(
                "/topic/chat-room/" + chatRoom.getId(),
                messageDTO);

        // Send notification to mentioned users
        if (request.getMentionedUserIds() != null) {
            for (Long mentionedUserId : request.getMentionedUserIds()) {
                messagingTemplate.convertAndSendToUser(
                        mentionedUserId.toString(),
                        "/queue/mention",
                        messageDTO);
            }
        }

        log.info("Message sent in chat room {} by user {}", chatRoom.getId(), senderId);
        return messageDTO;
    }

    @Override
    public ChatMessageDTO editMessage(Long messageId, String newContent, Long userId) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (!message.getSender().getId().equals(userId)) {
            throw new ForbiddenException("You can only edit your own messages");
        }

        if (message.getIsDeleted()) {
            throw new BadRequestException("Cannot edit deleted message");
        }

        message.edit(newContent);
        message = messageRepository.save(message);

        // Broadcast update
        ChatMessageDTO messageDTO = messageMapper.toDTO(message);
        messagingTemplate.convertAndSend(
                "/topic/chat-room/" + message.getChatRoom().getId() + "/edit",
                messageDTO);

        log.info("Message {} edited by user {}", messageId, userId);
        return messageDTO;
    }

    @Override
    public void deleteMessage(Long messageId, Long userId) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        // Check permission
        ChatParticipant participant = participantRepository
                .findByChatRoomIdAndUserId(message.getChatRoom().getId(), userId)
                .orElseThrow(() -> new ForbiddenException("You are not a participant"));

        boolean canDelete = message.getSender().getId().equals(userId) || participant.isAdmin();

        if (!canDelete) {
            throw new ForbiddenException("You cannot delete this message");
        }

        message.softDelete();
        messageRepository.save(message);

        // Broadcast deletion
        messagingTemplate.convertAndSend(
                "/topic/chat-room/" + message.getChatRoom().getId() + "/delete",
                messageId);

        log.info("Message {} deleted by user {}", messageId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDTO> getChatRoomMessages(Long chatRoomId, Long userId, Pageable pageable) {
        // Check if user is participant
        if (!participantRepository.existsActiveByChatRoomIdAndUserId(chatRoomId, userId)) {
            throw new ForbiddenException("You are not a participant of this chat room");
        }

        Page<ChatMessage> messages = messageRepository.findActiveByChatRoomId(chatRoomId, pageable);
        return messages.map(messageMapper::toDTOWithParent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> searchMessages(Long chatRoomId, String searchTerm, Long userId) {
        // Check if user is participant
        if (!participantRepository.existsActiveByChatRoomIdAndUserId(chatRoomId, userId)) {
            throw new ForbiddenException("You are not a participant of this chat room");
        }

        List<ChatMessage> messages = messageRepository.searchInChatRoom(
                chatRoomId, searchTerm, Pageable.ofSize(50));

        return messages.stream()
                .map(messageMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ===== MESSAGE ACTIONS =====

    @Override
    public void pinMessage(Long messageId, Long userId) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        ChatParticipant participant = participantRepository
                .findByChatRoomIdAndUserId(message.getChatRoom().getId(), userId)
                .orElseThrow(() -> new ForbiddenException("You are not a participant"));

        if (!participant.isAdmin()) {
            throw new ForbiddenException("Only admins can pin messages");
        }

        message.pin(userId);
        messageRepository.save(message);

        // Broadcast pin
        messagingTemplate.convertAndSend(
                "/topic/chat-room/" + message.getChatRoom().getId() + "/pin",
                messageMapper.toDTO(message));

        log.info("Message {} pinned by user {}", messageId, userId);
    }

    @Override
    public void unpinMessage(Long messageId, Long userId) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        ChatParticipant participant = participantRepository
                .findByChatRoomIdAndUserId(message.getChatRoom().getId(), userId)
                .orElseThrow(() -> new ForbiddenException("You are not a participant"));

        if (!participant.isAdmin()) {
            throw new ForbiddenException("Only admins can unpin messages");
        }

        message.unpin();
        messageRepository.save(message);

        // Broadcast unpin
        messagingTemplate.convertAndSend(
                "/topic/chat-room/" + message.getChatRoom().getId() + "/unpin",
                messageId);

        log.info("Message {} unpinned by user {}", messageId, userId);
    }

    @Override
    public void reactToMessage(Long messageId, String emoji, Long userId) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if reaction already exists
        boolean exists = reactionRepository.existsByMessageIdAndUserIdAndEmoji(messageId, userId, emoji);
        if (exists) {
            return; // Already reacted
        }

        MessageReaction reaction = MessageReaction.builder()
                .message(message)
                .user(user)
                .emoji(emoji)
                .build();

        reactionRepository.save(reaction);

        // Broadcast reaction
        messagingTemplate.convertAndSend(
                "/topic/chat-room/" + message.getChatRoom().getId() + "/reaction",
                Map.of(
                        "messageId", messageId,
                        "userId", userId,
                        "emoji", emoji,
                        "action", "add"));

        log.info("User {} reacted to message {} with {}", userId, messageId, emoji);
    }

    @Override
    public void removeReaction(Long messageId, String emoji, Long userId) {
        MessageReaction reaction = reactionRepository
                .findByMessageIdAndUserIdAndEmoji(messageId, userId, emoji)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found"));

        Long chatRoomId = reaction.getMessage().getChatRoom().getId();
        reactionRepository.delete(reaction);

        // Broadcast reaction removal
        messagingTemplate.convertAndSend(
                "/topic/chat-room/" + chatRoomId + "/reaction",
                Map.of(
                        "messageId", messageId,
                        "userId", userId,
                        "emoji", emoji,
                        "action", "remove"));

        log.info("User {} removed reaction from message {}", userId, messageId);
    }

    // ===== READ RECEIPTS =====

    @Override
    public void markAsRead(Long chatRoomId, Long userId) {
        ChatParticipant participant = participantRepository
                .findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        participant.markAsRead();
        participantRepository.save(participant);

        // Mark all messages as read
        List<ChatMessage> unreadMessages = messageRepository.findUnreadMessages(
                chatRoomId, userId,
                participant.getLastReadAt() != null ? participant.getLastReadAt() : LocalDateTime.MIN);

        for (ChatMessage message : unreadMessages) {
            markMessageAsRead(message.getId(), userId);
        }

        log.info("User {} marked chat room {} as read", userId, chatRoomId);
    }

    @Override
    public void markMessageAsRead(Long messageId, Long userId) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Don't create read receipt for own messages
        if (message.getSender().getId().equals(userId)) {
            return;
        }

        // Check if already read
        boolean alreadyRead = readReceiptRepository
                .existsByMessageIdAndUserId(messageId, userId);

        if (!alreadyRead) {
            MessageReadReceipt receipt = MessageReadReceipt.builder()
                    .message(message)
                    .user(user)
                    .build();

            readReceiptRepository.save(receipt);

            // Broadcast read receipt
            messagingTemplate.convertAndSend(
                    "/topic/chat-room/" + message.getChatRoom().getId() + "/read",
                    Map.of(
                            "messageId", messageId,
                            "userId", userId,
                            "readAt", LocalDateTime.now()));
        }
    }

    // ===== TYPING INDICATOR =====

    @Override
    public void sendTypingIndicator(Long chatRoomId, Long userId, Boolean isTyping) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TypingIndicatorDTO indicator = TypingIndicatorDTO.builder()
                .chatRoomId(chatRoomId)
                .userId(userId)
                .userName(user.getName())
                .isTyping(isTyping)
                .build();

        messagingTemplate.convertAndSend(
                "/topic/chat-room/" + chatRoomId + "/typing",
                indicator);
    }

    // ===== HELPER METHODS =====

    private void sendSystemMessage(Long chatRoomId, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));

        // Get system user or use first admin
        User systemUser = chatRoom.getParticipants().stream()
                .filter(p -> p.getRole() == ChatRole.OWNER)
                .findFirst()
                .map(ChatParticipant::getUser)
                .orElseThrow(() -> new ResourceNotFoundException("No owner found"));

        ChatMessage systemMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(systemUser)
                .messageType(MessageType.SYSTEM)
                .content(content)
                .build();

        messageRepository.save(systemMessage);

        // Broadcast system message
        messagingTemplate.convertAndSend(
                "/topic/chat-room/" + chatRoomId,
                messageMapper.toDTO(systemMessage));
    }

    private void updateUnreadCounts(Long chatRoomId, Long senderId) {
        List<ChatParticipant> participants = participantRepository
                .findActiveByChatRoomId(chatRoomId);

        for (ChatParticipant participant : participants) {
            if (!participant.getUser().getId().equals(senderId)) {
                participant.incrementUnreadCount();
                participantRepository.save(participant);
            }
        }
    }
}
