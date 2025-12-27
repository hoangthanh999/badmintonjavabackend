package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.ChatRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_participants", uniqueConstraints = @UniqueConstraint(columnNames = { "chat_room_id",
        "user_id" }), indexes = {
                @Index(name = "idx_chat_room_id", columnList = "chat_room_id"),
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_role", columnList = "role")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParticipant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ChatRole role = ChatRole.MEMBER;

    @Column(length = 20)
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, LEFT, KICKED, BANNED

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "unread_count")
    @Builder.Default
    private Integer unreadCount = 0;

    @Column(name = "is_muted")
    @Builder.Default
    private Boolean isMuted = false;

    @Column(name = "muted_until")
    private LocalDateTime mutedUntil;

    @Column(name = "is_pinned")
    @Builder.Default
    private Boolean isPinned = false;

    @Column(name = "custom_nickname", length = 100)
    private String customNickname;

    @Column(name = "notification_enabled")
    @Builder.Default
    private Boolean notificationEnabled = true;

    @Column(name = "added_by_user_id")
    private Long addedByUserId;

    @Column(name = "kicked_by_user_id")
    private Long kickedByUserId;

    @Column(name = "kick_reason", columnDefinition = "TEXT")
    private String kickReason;

    // === HELPER METHODS ===

    @PrePersist
    public void setJoinedAt() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
    }

    public void leave() {
        this.status = "LEFT";
        this.leftAt = LocalDateTime.now();
    }

    public void kick(Long kickedBy, String reason) {
        this.status = "KICKED";
        this.leftAt = LocalDateTime.now();
        this.kickedByUserId = kickedBy;
        this.kickReason = reason;
    }

    public void ban(Long bannedBy, String reason) {
        this.status = "BANNED";
        this.leftAt = LocalDateTime.now();
        this.kickedByUserId = bannedBy;
        this.kickReason = reason;
    }

    public void markAsRead() {
        this.lastReadAt = LocalDateTime.now();
        this.unreadCount = 0;
    }

    public void incrementUnreadCount() {
        this.unreadCount++;
    }

    public void updateLastSeen() {
        this.lastSeenAt = LocalDateTime.now();
    }

    public void mute(Integer hours) {
        this.isMuted = true;
        if (hours != null && hours > 0) {
            this.mutedUntil = LocalDateTime.now().plusHours(hours);
        }
    }

    public void unmute() {
        this.isMuted = false;
        this.mutedUntil = null;
    }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public boolean isAdmin() {
        return role == ChatRole.ADMIN || role == ChatRole.OWNER;
    }

    public boolean canSendMessage() {
        return isActive() && !isMuted();
    }

    public boolean isMuted() {
        if (!isMuted)
            return false;
        if (mutedUntil == null)
            return true;
        if (mutedUntil.isAfter(LocalDateTime.now()))
            return true;
        // Auto unmute if expired
        unmute();
        return false;
    }

    public void promoteToAdmin() {
        if (role == ChatRole.MEMBER) {
            this.role = ChatRole.ADMIN;
        }
    }

    public void demoteToMember() {
        if (role == ChatRole.ADMIN) {
            this.role = ChatRole.MEMBER;
        }
    }

    public String getDisplayName() {
        return customNickname != null ? customNickname : user.getName();
    }
}
