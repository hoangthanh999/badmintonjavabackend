package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.UserAddress;
import com.hoangthanhhong.badminton.enums.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    // === BASIC QUERIES ===

    Optional<UserAddress> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT ua FROM UserAddress ua WHERE ua.user.id = :userId AND ua.deletedAt IS NULL ORDER BY ua.isDefault DESC, ua.createdAt DESC")
    List<UserAddress> findByUserId(@Param("userId") Long userId);

    @Query("SELECT ua FROM UserAddress ua WHERE ua.user.id = :userId AND ua.isActive = true AND ua.deletedAt IS NULL ORDER BY ua.isDefault DESC")
    List<UserAddress> findActiveByUserId(@Param("userId") Long userId);

    // === DEFAULT ADDRESS QUERIES ===

    @Query("SELECT ua FROM UserAddress ua WHERE ua.user.id = :userId AND ua.isDefault = true AND ua.deletedAt IS NULL")
    Optional<UserAddress> findDefaultByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(ua) > 0 THEN true ELSE false END FROM UserAddress ua WHERE ua.user.id = :userId AND ua.isDefault = true AND ua.deletedAt IS NULL")
    boolean existsDefaultAddressByUserId(@Param("userId") Long userId);

    // === TYPE QUERIES ===

    @Query("SELECT ua FROM UserAddress ua WHERE ua.user.id = :userId AND ua.type = :type AND ua.deletedAt IS NULL")
    List<UserAddress> findByUserIdAndType(@Param("userId") Long userId, @Param("type") AddressType type);

    // === UPDATE DEFAULT ADDRESS ===

    @Modifying
    @Query("UPDATE UserAddress ua SET ua.isDefault = false WHERE ua.user.id = :userId AND ua.deletedAt IS NULL")
    void unsetAllDefaultByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserAddress ua SET ua.isDefault = true WHERE ua.id = :addressId")
    void setDefaultById(@Param("addressId") Long addressId);

    // === COUNT QUERIES ===

    @Query("SELECT COUNT(ua) FROM UserAddress ua WHERE ua.user.id = :userId AND ua.deletedAt IS NULL")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(ua) FROM UserAddress ua WHERE ua.user.id = :userId AND ua.isActive = true AND ua.deletedAt IS NULL")
    Long countActiveByUserId(@Param("userId") Long userId);

    // === LOCATION QUERIES ===

    @Query("SELECT ua FROM UserAddress ua WHERE ua.city = :city AND ua.deletedAt IS NULL")
    List<UserAddress> findByCity(@Param("city") String city);

    @Query("SELECT ua FROM UserAddress ua WHERE ua.city = :city AND ua.district = :district AND ua.deletedAt IS NULL")
    List<UserAddress> findByCityAndDistrict(@Param("city") String city, @Param("district") String district);

    @Query("""
                SELECT ua FROM UserAddress ua
                WHERE ua.user.id = :userId
                AND ua.id = :addressId
                AND ua.deletedAt IS NULL
            """)
    Optional<UserAddress> findByIdAndUserId(@Param("addressId") Long addressId, @Param("userId") Long userId);

    // === COORDINATES QUERIES ===

    @Query("SELECT ua FROM UserAddress ua WHERE ua.latitude IS NOT NULL AND ua.longitude IS NOT NULL AND ua.deletedAt IS NULL")
    List<UserAddress> findAllWithCoordinates();

    @Query("""
                SELECT ua FROM UserAddress ua
                WHERE ua.user.id = :userId
                AND ua.latitude IS NOT NULL
                AND ua.longitude IS NOT NULL
                AND ua.deletedAt IS NULL
            """)
    List<UserAddress> findWithCoordinatesByUserId(@Param("userId") Long userId);

    // === STATISTICS ===

    @Query("""
                SELECT ua.type, COUNT(ua)
                FROM UserAddress ua
                WHERE ua.deletedAt IS NULL
                GROUP BY ua.type
            """)
    List<Object[]> countByType();

    @Query("""
                SELECT ua.city, COUNT(ua)
                FROM UserAddress ua
                WHERE ua.deletedAt IS NULL
                GROUP BY ua.city
                ORDER BY COUNT(ua) DESC
            """)
    List<Object[]> countByCity();
}