package com.hoangthanhhong.badminton.dto.chat;

import com.hoangthanhhong.badminton.enums.ChatRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParticipantDTO {

    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String userEmail;
    private ChatRole role;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSeenAt;

    private Integer unreadCount;
    private Boolean isMuted;
    private String customNickname;
    private Boolean notificationEnabled;
}
