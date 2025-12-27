package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_reactions", uniqueConstraints = @UniqueConstraint(columnNames = { "message_id", "user_id",
        "emoji" }), indexes = {
                @Index(name = "idx_message_id", columnList = "message_id"),
                @Index(name = "idx_user_id", columnList = "user_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private ChatMessage message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 10)
    private String emoji; // 👍, ❤️, 😂, 😮, 😢, 😡

    @Column(name = "reacted_at", nullable = false)
    private LocalDateTime reactedAt;

    @PrePersist
    public void setReactedAt() {
        if (reactedAt == null) {
            reactedAt = LocalDateTime.now();
        }
    }
}
