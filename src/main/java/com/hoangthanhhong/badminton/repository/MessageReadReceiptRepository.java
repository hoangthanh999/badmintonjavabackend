package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.MessageReadReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageReadReceiptRepository extends JpaRepository<MessageReadReceipt, Long> {

    List<MessageReadReceipt> findByMessageId(Long messageId);

    boolean existsByMessageIdAndUserId(Long messageId, Long userId);

    @Query("""
                SELECT COUNT(r)
                FROM MessageReadReceipt r
                WHERE r.message.id = :messageId
            """)
    Long countByMessageId(@Param("messageId") Long messageId);

    @Query("""
                SELECT r.user.id
                FROM MessageReadReceipt r
                WHERE r.message.id = :messageId
            """)
    List<Long> findUserIdsWhoReadMessage(@Param("messageId") Long messageId);
}
