package com.hoangthanhhong.badminton.dto.chat;

import com.hoangthanhhong.badminton.enums.ChatRoomType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDTO {

    private Long id;
    private String name;
    private String description;
    private ChatRoomType type;
    private String avatar;
    private String status;
    private Integer maxMembers;
    private Boolean isLocked;
    private Boolean isPrivate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageAt;

    private String lastMessagePreview;
    private Long totalMessages;
    private String roomCode;

    // Participants info
    private Integer participantsCount;
    private List<ChatParticipantDTO> participants;

    // Last message
    private ChatMessageDTO lastMessage;

    // User-specific info
    private Integer unreadCount;
    private Boolean isMuted;
    private Boolean isPinned;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
