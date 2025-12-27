package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.ChatRoom;
import com.hoangthanhhong.badminton.enums.ChatRoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomCode(String roomCode);

    List<ChatRoom> findByType(ChatRoomType type);

    List<ChatRoom> findByStatus(String status);

    // === COMPLEX QUERIES ===

    // 1. Tìm tất cả phòng chat của user
    @Query("""
                SELECT DISTINCT cr FROM ChatRoom cr
                JOIN cr.participants p
                WHERE p.user.id = :userId
                AND p.status = 'ACTIVE'
                AND cr.status = 'ACTIVE'
                AND cr.deletedAt IS NULL
                ORDER BY cr.lastMessageAt DESC NULLS LAST
            """)
    List<ChatRoom> findUserChatRooms(@Param("userId") Long userId);

    @Query("""
                SELECT DISTINCT cr FROM ChatRoom cr
                JOIN cr.participants p
                WHERE p.user.id = :userId
                AND p.status = 'ACTIVE'
                AND cr.status = 'ACTIVE'
                AND cr.deletedAt IS NULL
                ORDER BY cr.lastMessageAt DESC NULLS LAST
            """)
    Page<ChatRoom> findUserChatRooms(@Param("userId") Long userId, Pageable pageable);

    // 2. Tìm chat room 1-1 giữa 2 users
    @Query("""
                SELECT cr FROM ChatRoom cr
                WHERE cr.type = 'DIRECT'
                AND cr.id IN (
                    SELECT p1.chatRoom.id FROM ChatParticipant p1
                    WHERE p1.user.id = :userId1
                    AND p1.status = 'ACTIVE'
                    AND p1.chatRoom.id IN (
                        SELECT p2.chatRoom.id FROM ChatParticipant p2
                        WHERE p2.user.id = :userId2
                        AND p2.status = 'ACTIVE'
                    )
                )
                AND cr.deletedAt IS NULL
            """)
    Optional<ChatRoom> findDirectChatBetweenUsers(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2);

    // 3. Tìm kiếm phòng chat theo tên
    @Query("""
                SELECT DISTINCT cr FROM ChatRoom cr
                JOIN cr.participants p
                WHERE p.user.id = :userId
                AND p.status = 'ACTIVE'
                AND LOWER(cr.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                AND cr.status = 'ACTIVE'
                AND cr.deletedAt IS NULL
                ORDER BY cr.lastMessageAt DESC
            """)
    List<ChatRoom> searchUserChatRooms(
            @Param("userId") Long userId,
            @Param("searchTerm") String searchTerm);

    // 4. Đếm số phòng chat chưa đọc
    @Query("""
                SELECT COUNT(DISTINCT cr) FROM ChatRoom cr
                JOIN cr.participants p
                WHERE p.user.id = :userId
                AND p.status = 'ACTIVE'
                AND p.unreadCount > 0
                AND cr.status = 'ACTIVE'
                AND cr.deletedAt IS NULL
            """)
    Long countUnreadChatRooms(@Param("userId") Long userId);

    // 5. Lấy phòng chat theo booking
    @Query("""
                SELECT cr FROM ChatRoom cr
                WHERE cr.relatedBooking.id = :bookingId
                AND cr.deletedAt IS NULL
            """)
    Optional<ChatRoom> findByBookingId(@Param("bookingId") Long bookingId);

    // 6. Lấy phòng chat theo tournament
    @Query("""
                SELECT cr FROM ChatRoom cr
                WHERE cr.relatedTournament.id = :tournamentId
                AND cr.deletedAt IS NULL
            """)
    Optional<ChatRoom> findByTournamentId(@Param("tournamentId") Long tournamentId);

    // 7. Lấy các phòng chat public
    @Query("""
                SELECT cr FROM ChatRoom cr
                WHERE cr.isPrivate = false
                AND cr.status = 'ACTIVE'
                AND cr.deletedAt IS NULL
                ORDER BY cr.totalMessages DESC
            """)
    Page<ChatRoom> findPublicChatRooms(Pageable pageable);

    // 8. Lấy phòng chat phổ biến
    @Query("""
                SELECT cr FROM ChatRoom cr
                WHERE cr.type = 'GROUP'
                AND cr.isPrivate = false
                AND cr.status = 'ACTIVE'
                AND cr.deletedAt IS NULL
                ORDER BY SIZE(cr.participants) DESC, cr.totalMessages DESC
            """)
    List<ChatRoom> findPopularChatRooms(Pageable pageable);
}
