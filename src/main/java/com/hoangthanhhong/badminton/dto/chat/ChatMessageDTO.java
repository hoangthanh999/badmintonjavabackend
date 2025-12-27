package com.hoangthanhhong.badminton.dto.chat;

import com.hoangthanhhong.badminton.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {

    private Long id;
    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private MessageType messageType;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime editedAt;

    private Boolean isEdited;
    private Boolean isDeleted;
    private Boolean isPinned;

    // File/Media info
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String thumbnailUrl;
    private Integer duration;

    // Reply info
    private Long parentMessageId;
    private ChatMessageDTO parentMessage;
    private Boolean isForwarded;

    // Reactions
    private List<MessageReactionDTO> reactions;
    private Integer totalReactions;

    // Read receipts
    private Integer readCount;
    private Boolean isRead;

    // Mentions
    private List<Long> mentionedUserIds;

    // Metadata
    private String metadata;
}
