package com.hoangthanhhong.badminton.dto.request.chat;

import com.hoangthanhhong.badminton.enums.MessageType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChatMessageRequest {

    @NotNull(message = "Chat room ID is required")
    private Long chatRoomId;

    @NotNull(message = "Message type is required")
    private MessageType messageType;

    private String content;

    // File/Media info
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String thumbnailUrl;
    private Integer duration;

    // Reply
    private Long parentMessageId;

    // Forward
    private Boolean isForwarded;
    private Long originalMessageId;

    // Mentions
    private List<Long> mentionedUserIds;

    // Metadata
    private String metadata;
}
