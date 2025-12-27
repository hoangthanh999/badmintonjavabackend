package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.ChatParticipant;
import com.hoangthanhhong.badminton.enums.ChatRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    List<ChatParticipant> findByChatRoomId(Long chatRoomId);

    List<ChatParticipant> findByUserId(Long userId);

    Optional<ChatParticipant> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);

    @Query("""
                SELECT p FROM ChatParticipant p
                WHERE p.chatRoom.id = :chatRoomId
                AND p.status = 'ACTIVE'
            """)
    List<ChatParticipant> findActiveByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Query("""
                SELECT COUNT(p) FROM ChatParticipant p
                WHERE p.chatRoom.id = :chatRoomId
                AND p.status = 'ACTIVE'
            """)
    Long countActiveByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Query("""
                SELECT p FROM ChatParticipant p
                WHERE p.chatRoom.id = :chatRoomId
                AND p.role IN :roles
                AND p.status = 'ACTIVE'
            """)
    List<ChatParticipant> findByRoles(
            @Param("chatRoomId") Long chatRoomId,
            @Param("roles") List<ChatRole> roles);

    @Query("""
                SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
                FROM ChatParticipant p
                WHERE p.chatRoom.id = :chatRoomId
                AND p.user.id = :userId
                AND p.status = 'ACTIVE'
            """)
    boolean existsActiveByChatRoomIdAndUserId(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId);
}
