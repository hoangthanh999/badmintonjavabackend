package com.hoangthanhhong.badminton.mapper;

import com.hoangthanhhong.badminton.dto.chat.ChatParticipantDTO;
import com.hoangthanhhong.badminton.entity.ChatParticipant;
import org.springframework.stereotype.Component;

@Component
public class ChatParticipantMapper {

    public ChatParticipantDTO toDTO(ChatParticipant participant) {
        if (participant == null)
            return null;

        return ChatParticipantDTO.builder()
                .id(participant.getId())
                .userId(participant.getUser().getId())
                .userName(participant.getUser().getName())
                .userAvatar(participant.getUser().getAvatar())
                .userEmail(participant.getUser().getEmail())
                .role(participant.getRole())
                .status(participant.getStatus())
                .joinedAt(participant.getJoinedAt())
                .lastSeenAt(participant.getLastSeenAt())
                .unreadCount(participant.getUnreadCount())
                .isMuted(participant.getIsMuted())
                .customNickname(participant.getCustomNickname())
                .notificationEnabled(participant.getNotificationEnabled())
                .build();
    }
}
