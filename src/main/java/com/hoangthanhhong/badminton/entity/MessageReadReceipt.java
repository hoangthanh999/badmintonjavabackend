package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_read_receipts", uniqueConstraints = @UniqueConstraint(columnNames = { "message_id",
        "user_id" }), indexes = {
                @Index(name = "idx_message_id", columnList = "message_id"),
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_read_at", columnList = "read_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReadReceipt extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private ChatMessage message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;

    @PrePersist
    public void setReadAt() {
        if (readAt == null) {
            readAt = LocalDateTime.now();
        }
    }
}
