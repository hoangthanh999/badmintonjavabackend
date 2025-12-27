package com.hoangthanhhong.badminton.dto.request.chat;

import com.hoangthanhhong.badminton.enums.ChatRoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChatRoomRequest {

    @NotBlank(message = "Room name is required")
    private String name;

    private String description;

    @NotNull(message = "Room type is required")
    private ChatRoomType type;

    private String avatar;

    private Integer maxMembers;

    private Boolean isPrivate;

    // List of user IDs to add as participants
    private List<Long> participantUserIds;

    // Related entities
    private Long relatedBookingId;
    private Long relatedTournamentId;
}
