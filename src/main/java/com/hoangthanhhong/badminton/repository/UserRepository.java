package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.User;
import com.hoangthanhhong.badminton.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // === BASIC QUERIES ===

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> findByStatus(UserStatus status);

    Page<User> findByStatus(UserStatus status, Pageable pageable);

    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    // === COMPLEX QUERIES ===

    // 1. Tìm user theo role
    @Query("SELECT u FROM User u WHERE u.role.id = :roleId AND u.deletedAt IS NULL")
    List<User> findByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT u FROM User u WHERE u.role.roleName = :roleName AND u.deletedAt IS NULL")
    List<User> findByRole_Name(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u WHERE u.role.roleName IN :roleNames AND u.deletedAt IS NULL")
    List<User> findByRole_NameIn(@Param("roleNames") List<String> roleNames);

    // 2. Tìm kiếm user
    @Query("""
                SELECT u FROM User u
                WHERE (
                    LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                    OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                )
                AND (:status IS NULL OR u.status = :status)
                AND u.deletedAt IS NULL
                ORDER BY u.createdAt DESC
            """)
    Page<User> searchUsers(
            @Param("searchTerm") String searchTerm,
            @Param("status") UserStatus status,
            Pageable pageable);

    // 3. Đếm user theo role
    @Query("SELECT COUNT(u) FROM User u WHERE u.role.id = :roleId AND u.deletedAt IS NULL")
    Long countByRoleId(@Param("roleId") Long roleId);

    // 4. Tìm user active
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND u.deletedAt IS NULL")
    List<User> findAllActiveUsers();

    // 5. Tìm user theo email verified
    @Query("SELECT u FROM User u WHERE u.emailVerified = :verified AND u.deletedAt IS NULL")
    List<User> findByEmailVerified(@Param("verified") Boolean verified);

    // 6. Top users theo booking
    @Query("""
                SELECT u, COUNT(b) as bookingCount
                FROM User u
                LEFT JOIN u.bookings b
                WHERE u.deletedAt IS NULL
                GROUP BY u
                ORDER BY bookingCount DESC
            """)
    List<Object[]> findTopUsersByBookings(Pageable pageable);

    // 7. Tìm user theo loyalty points
    @Query("""
                SELECT u FROM User u
                WHERE u.deletedAt IS NULL
                ORDER BY (
                    SELECT SUM(lp.points)
                    FROM LoyaltyPoint lp
                    WHERE lp.user.id = u.id
                ) DESC
            """)
    List<User> findTopUsersByLoyaltyPoints(Pageable pageable);
}
