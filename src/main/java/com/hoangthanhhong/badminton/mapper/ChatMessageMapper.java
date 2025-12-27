package com.hoangthanhhong.badminton.mapper;

import com.hoangthanhhong.badminton.dto.chat.ChatMessageDTO;
import com.hoangthanhhong.badminton.dto.request.chat.CreateChatMessageRequest;
import com.hoangthanhhong.badminton.entity.ChatMessage;
import com.hoangthanhhong.badminton.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ChatMessageMapper {

    public ChatMessageDTO toDTO(ChatMessage message) {
        if (message == null)
            return null;

        return ChatMessageDTO.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoom().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getName())
                .senderAvatar(message.getSender().getAvatar())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .editedAt(message.getEditedAt())
                .isEdited(message.getIsEdited())
                .isDeleted(message.getIsDeleted())
                .isPinned(message.getIsPinned())
                .fileUrl(message.getFileUrl())
                .fileName(message.getFileName())
                .fileSize(message.getFileSize())
                .fileType(message.getFileType())
                .thumbnailUrl(message.getThumbnailUrl())
                .duration(message.getDuration())
                .parentMessageId(message.getParentMessage() != null ? message.getParentMessage().getId() : null)
                .isForwarded(message.getIsForwarded())
                .totalReactions(message.getReactions().size())
                .readCount(message.getReadCount())
                .mentionedUserIds(message.getMentionedUsers().stream()
                        .map(User::getId)
                        .collect(Collectors.toList()))
                .metadata(message.getMetadata())
                .build();
    }

    public ChatMessageDTO toDTOWithParent(ChatMessage message) {
        ChatMessageDTO dto = toDTO(message);
        if (message.getParentMessage() != null) {
            dto.setParentMessage(toDTO(message.getParentMessage()));
        }
        return dto;
    }
}
