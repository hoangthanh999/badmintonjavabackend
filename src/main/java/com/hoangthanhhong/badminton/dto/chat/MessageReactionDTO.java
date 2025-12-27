package com.hoangthanhhong.badminton.dto.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReactionDTO {

    private Long id;
    private Long messageId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String emoji;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reactedAt;
}
