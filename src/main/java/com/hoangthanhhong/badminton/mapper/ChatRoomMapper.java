package com.hoangthanhhong.badminton.mapper;

import com.hoangthanhhong.badminton.dto.chat.ChatRoomDTO;
import com.hoangthanhhong.badminton.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatRoomMapper {

    private final ChatParticipantMapper participantMapper;
    private final ChatMessageMapper messageMapper;

    public ChatRoomDTO toDTO(ChatRoom chatRoom) {
        if (chatRoom == null)
            return null;

        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .description(chatRoom.getDescription())
                .type(chatRoom.getType())
                .avatar(chatRoom.getAvatar())
                .status(chatRoom.getStatus())
                .maxMembers(chatRoom.getMaxMembers())
                .isLocked(chatRoom.getIsLocked())
                .isPrivate(chatRoom.getIsPrivate())
                .lastMessageAt(chatRoom.getLastMessageAt())
                .lastMessagePreview(chatRoom.getLastMessagePreview())
                .totalMessages(chatRoom.getTotalMessages())
                .roomCode(chatRoom.getRoomCode())
                .participantsCount(chatRoom.getActiveParticipantsCount())
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }

    public ChatRoomDTO toDTOWithDetails(ChatRoom chatRoom, Long currentUserId) {
        ChatRoomDTO dto = toDTO(chatRoom);

        // Add participants
        dto.setParticipants(chatRoom.getParticipants().stream()
                .filter(p -> "ACTIVE".equals(p.getStatus()))
                .map(participantMapper::toDTO)
                .collect(Collectors.toList()));

        // Add last message
        if (!chatRoom.getMessages().isEmpty()) {
            dto.setLastMessage(messageMapper.toDTO(chatRoom.getMessages().get(0)));
        }

        // Add user-specific info
        chatRoom.getParticipants().stream()
                .filter(p -> p.getUser().getId().equals(currentUserId))
                .findFirst()
                .ifPresent(participant -> {
                    dto.setUnreadCount(participant.getUnreadCount());
                    dto.setIsMuted(participant.getIsMuted());
                    dto.setIsPinned(participant.getIsPinned());
                });

        return dto;
    }
}
