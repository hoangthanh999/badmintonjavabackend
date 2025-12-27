package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.MessageReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {

    List<MessageReaction> findByMessageId(Long messageId);

    Optional<MessageReaction> findByMessageIdAndUserIdAndEmoji(
            Long messageId, Long userId, String emoji);

    boolean existsByMessageIdAndUserIdAndEmoji(
            Long messageId, Long userId, String emoji);

    void deleteByMessageIdAndUserIdAndEmoji(
            Long messageId, Long userId, String emoji);

    @Query("""
                SELECT r.emoji, COUNT(r) as count
                FROM MessageReaction r
                WHERE r.message.id = :messageId
                GROUP BY r.emoji
                ORDER BY count DESC
            """)
    List<Object[]> getReactionSummary(@Param("messageId") Long messageId);
}
