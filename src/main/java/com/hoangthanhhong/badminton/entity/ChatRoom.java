package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.ChatRoomType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_rooms", indexes = {
        @Index(name = "idx_type", columnList = "type"),
        @Index(name = "idx_created_by", columnList = "created_by"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ChatRoomType type = ChatRoomType.GROUP;

    @Column(length = 500)
    private String avatar;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    @Column(length = 20)
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, ARCHIVED, DELETED

    @Column(name = "max_members")
    @Builder.Default
    private Integer maxMembers = 100;

    @Column(name = "is_locked")
    @Builder.Default
    private Boolean isLocked = false;

    @Column(name = "is_private")
    @Builder.Default
    private Boolean isPrivate = false;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "last_message_preview", length = 500)
    private String lastMessagePreview;

    @Column(name = "total_messages")
    @Builder.Default
    private Long totalMessages = 0L;

    @Column(name = "room_code", unique = true, length = 50)
    private String roomCode; // For joining by code

    // === RELATIONSHIPS ===

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sentAt DESC")
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_booking_id")
    private Booking relatedBooking; // Nếu chat liên quan đến booking

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_tournament_id")
    private Tournament relatedTournament; // Nếu chat liên quan đến giải đấu

    // === HELPER METHODS ===

    @PrePersist
    public void generateRoomCode() {
        if (roomCode == null) {
            roomCode = "ROOM" + System.currentTimeMillis();
        }
    }

    public void addParticipant(ChatParticipant participant) {
        if (participants.size() >= maxMembers) {
            throw new IllegalStateException("Chat room is full");
        }
        participants.add(participant);
        participant.setChatRoom(this);
    }

    public void removeParticipant(ChatParticipant participant) {
        participants.remove(participant);
        participant.setChatRoom(null);
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        message.setChatRoom(this);
        this.lastMessageAt = LocalDateTime.now();
        this.lastMessagePreview = message.getContent();
        this.totalMessages++;
    }

    public Integer getActiveParticipantsCount() {
        return (int) participants.stream()
                .filter(p -> "ACTIVE".equals(p.getStatus()))
                .count();
    }

    public boolean isFull() {
        return getActiveParticipantsCount() >= maxMembers;
    }

    public boolean isUserParticipant(Long userId) {
        return participants.stream()
                .anyMatch(p -> p.getUser().getId().equals(userId) && "ACTIVE".equals(p.getStatus()));
    }

    public boolean canUserJoin(Long userId) {
        return !isFull() && !isLocked && !isUserParticipant(userId);
    }

    public void archive() {
        this.status = "ARCHIVED";
    }

    public void activate() {
        this.status = "ACTIVE";
    }

    public void lock() {
        this.isLocked = true;
    }

    public void unlock() {
        this.isLocked = false;
    }

    public boolean isDirect() {
        return type == ChatRoomType.DIRECT;
    }

    public boolean isGroup() {
        return type == ChatRoomType.GROUP;
    }

    public boolean isSupport() {
        return type == ChatRoomType.SUPPORT;
    }

    // Lấy tên hiển thị cho chat 1-1
    public String getDisplayNameForUser(Long userId) {
        if (type == ChatRoomType.DIRECT && participants.size() == 2) {
            return participants.stream()
                    .filter(p -> !p.getUser().getId().equals(userId))
                    .findFirst()
                    .map(p -> p.getUser().getName())
                    .orElse(name);
        }
        return name;
    }

    // Lấy avatar cho chat 1-1
    public String getAvatarForUser(Long userId) {
        if (type == ChatRoomType.DIRECT && participants.size() == 2) {
            return participants.stream()
                    .filter(p -> !p.getUser().getId().equals(userId))
                    .findFirst()
                    .map(p -> p.getUser().getAvatar())
                    .orElse(avatar);
        }
        return avatar;
    }
}
