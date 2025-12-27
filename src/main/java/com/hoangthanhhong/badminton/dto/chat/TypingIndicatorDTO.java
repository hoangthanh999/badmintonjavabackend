package com.hoangthanhhong.badminton.dto.chat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypingIndicatorDTO {

    private Long chatRoomId;
    private Long userId;
    private String userName;
    private Boolean isTyping;
}
