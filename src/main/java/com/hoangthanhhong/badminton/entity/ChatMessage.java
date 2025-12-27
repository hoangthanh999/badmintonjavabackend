package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_chat_room_id", columnList = "chat_room_id"),
        @Index(name = "idx_sender_id", columnList = "sender_id"),
        @Index(name = "idx_sent_at", columnList = "sent_at"),
        @Index(name = "idx_parent_message_id", columnList = "parent_message_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 20)
    @Builder.Default
    private MessageType messageType = MessageType.TEXT;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "is_edited")
    @Builder.Default
    private Boolean isEdited = false;

    @Column(name = "is_pinned")
    @Builder.Default
    private Boolean isPinned = false;

    @Column(name = "pinned_at")
    private LocalDateTime pinnedAt;

    @Column(name = "pinned_by_user_id")
    private Long pinnedByUserId;

    // === FILE/MEDIA INFORMATION ===

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize; // bytes

    @Column(name = "file_type", length = 100)
    private String fileType; // image/jpeg, video/mp4, etc.

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "duration")
    private Integer duration; // For audio/video in seconds

    // === REPLY/FORWARD ===

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id")
    private ChatMessage parentMessage; // For reply

    @Column(name = "is_forwarded")
    @Builder.Default
    private Boolean isForwarded = false;

    @Column(name = "original_message_id")
    private Long originalMessageId;

    // === REACTIONS ===

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MessageReaction> reactions = new ArrayList<>();

    // === READ RECEIPTS ===

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MessageReadReceipt> readReceipts = new ArrayList<>();

    // === MENTIONS ===

    @ManyToMany
    @JoinTable(name = "message_mentions", joinColumns = @JoinColumn(name = "message_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    private List<User> mentionedUsers = new ArrayList<>();

    // === METADATA (JSON) ===

    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON string for additional data

    // === HELPER METHODS ===

    @PrePersist
    public void setSentAt() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }

    public void edit(String newContent) {
        this.content = newContent;
        this.isEdited = true;
        this.editedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.content = "Message deleted";
    }

    public void pin(Long pinnedBy) {
        this.isPinned = true;
        this.pinnedAt = LocalDateTime.now();
        this.pinnedByUserId = pinnedBy;
    }

    public void unpin() {
        this.isPinned = false;
        this.pinnedAt = null;
        this.pinnedByUserId = null;
    }

    public void addReaction(MessageReaction reaction) {
        reactions.add(reaction);
        reaction.setMessage(this);
    }

    public void removeReaction(MessageReaction reaction) {
        reactions.remove(reaction);
        reaction.setMessage(null);
    }

    public void addReadReceipt(MessageReadReceipt receipt) {
        readReceipts.add(receipt);
        receipt.setMessage(this);
    }

    public boolean isRead() {
        return !readReceipts.isEmpty();
    }

    public Integer getReadCount() {
        return readReceipts.size();
    }

    public boolean isReadBy(Long userId) {
        return readReceipts.stream()
                .anyMatch(r -> r.getUser().getId().equals(userId));
    }

    public boolean hasReactions() {
        return !reactions.isEmpty();
    }

    public Integer getTotalReactions() {
        return reactions.size();
    }

    public boolean isReply() {
        return parentMessage != null;
    }

    public boolean hasAttachment() {
        return fileUrl != null && !fileUrl.isEmpty();
    }

    public boolean isImage() {
        return messageType == MessageType.IMAGE;
    }

    public boolean isVideo() {
        return messageType == MessageType.VIDEO;
    }

    public boolean isAudio() {
        return messageType == MessageType.AUDIO;
    }

    public boolean isFile() {
        return messageType == MessageType.FILE;
    }

    public boolean isSystemMessage() {
        return messageType == MessageType.SYSTEM;
    }

    public String getDisplayContent() {
        if (isDeleted) {
            return "Message deleted";
        }

        switch (messageType) {
            case IMAGE:
                return "📷 Image";
            case VIDEO:
                return "🎥 Video";
            case AUDIO:
                return "🎵 Audio";
            case FILE:
                return "📎 File: " + (fileName != null ? fileName : "Attachment");
            case LOCATION:
                return "📍 Location";
            case STICKER:
                return "Sticker";
            default:
                return content;
        }
    }
}
