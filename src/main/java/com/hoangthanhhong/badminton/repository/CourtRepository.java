package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.Court;
import com.hoangthanhhong.badminton.enums.CourtStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {

        // === BASIC QUERIES ===

        Optional<Court> findByIdAndDeletedAtIsNull(Long id);

        Optional<Court> findByName(String name);

        List<Court> findByStatus(CourtStatus status);

        Page<Court> findByStatus(CourtStatus status, Pageable pageable);

        @Query("SELECT c FROM Court c WHERE c.deletedAt IS NULL")
        List<Court> findAllActive();

        @Query("SELECT c FROM Court c WHERE c.deletedAt IS NULL")
        Page<Court> findAllActive(Pageable pageable);

        // === SEARCH QUERIES ===

        @Query("""
                            SELECT c FROM Court c
                            WHERE (
                                LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                                OR LOWER(c.location) LIKE LOWER(CONCAT('%', :keyword, '%'))
                                OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                            )
                            AND (:status IS NULL OR c.status = :status)
                            AND c.deletedAt IS NULL
                            ORDER BY c.rating DESC, c.name ASC
                        """)
        Page<Court> searchCourts(
                        @Param("keyword") String keyword,
                        @Param("status") CourtStatus status,
                        Pageable pageable);

        @Query("""
                            SELECT c FROM Court c
                            WHERE (:location IS NULL OR c.location = :location)
                            AND (:minPrice IS NULL OR c.basePrice >= :minPrice)
                            AND (:maxPrice IS NULL OR c.basePrice <= :maxPrice)
                            AND (:hasAirConditioning IS NULL OR c.hasAirConditioning = :hasAirConditioning)
                            AND (:minRating IS NULL OR c.rating >= :minRating)
                            AND c.status = 'AVAILABLE'
                            AND c.deletedAt IS NULL
                            ORDER BY c.rating DESC
                        """)
        Page<Court> findByFilters(
                        @Param("location") String location,
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        @Param("hasAirConditioning") Boolean hasAirConditioning,
                        @Param("minRating") Double minRating,
                        Pageable pageable);

        // === AVAILABILITY QUERIES ===

        @Query("""
                            SELECT c FROM Court c
                            WHERE c.status = 'AVAILABLE'
                            AND c.deletedAt IS NULL
                            AND c.id NOT IN (
                                SELECT b.court.id FROM Booking b
                                WHERE b.status IN ('CONFIRMED', 'PENDING')
                                AND b.date = :date
                                AND (
                                    (b.timeStart < :endTime AND b.timeEnd > :startTime)
                                )
                            )
                            ORDER BY c.rating DESC
                        """)
        List<Court> findAvailableCourts(
                        @Param("date") LocalDate date,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);

        @Query("""
                            SELECT c FROM Court c
                            WHERE c.status = 'AVAILABLE'
                            AND c.deletedAt IS NULL
                            AND c.id NOT IN (
                                SELECT b.court.id FROM Booking b
                                WHERE b.status IN ('CONFIRMED', 'PENDING')
                                AND b.date = :date
                                AND (
                                    (b.timeStart < :endTime AND b.timeEnd > :startTime)
                                )
                            )
                            ORDER BY c.rating DESC
                        """)
        Page<Court> findAvailableCourtsPage(
                        @Param("date") LocalDate date,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime,
                        Pageable pageable);

        @Query("""
                            SELECT CASE WHEN COUNT(b) > 0 THEN false ELSE true END
                            FROM Booking b
                            WHERE b.court.id = :courtId
                            AND b.status IN ('CONFIRMED', 'PENDING')
                            AND b.date = :date
                            AND (
                                (b.timeStart < :endTime AND b.timeEnd > :startTime)
                            )
                        """)
        boolean isCourtAvailable(
                        @Param("courtId") Long courtId,
                        @Param("date") LocalDate date,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);

        // === STATISTICS QUERIES ===

        @Query("SELECT COUNT(c) FROM Court c WHERE c.status = :status AND c.deletedAt IS NULL")
        Long countByStatus(@Param("status") CourtStatus status);

        @Query("SELECT COUNT(c) FROM Court c WHERE c.deletedAt IS NULL")
        Long countAllActive();

        @Query("""
                            SELECT c FROM Court c
                            WHERE c.deletedAt IS NULL
                            ORDER BY c.rating DESC
                        """)
        List<Court> findTopRatedCourts(Pageable pageable);

        @Query("""
                            SELECT c FROM Court c
                            LEFT JOIN c.bookings b
                            WHERE c.deletedAt IS NULL
                            GROUP BY c.id
                            ORDER BY COUNT(b) DESC
                        """)
        List<Court> findMostBookedCourts(Pageable pageable);

        @Query("""
                            SELECT AVG(c.rating) FROM Court c
                            WHERE c.rating IS NOT NULL
                            AND c.deletedAt IS NULL
                        """)
        Double getAverageRating();

        @Query("""
                            SELECT
                                COUNT(c) as total,
                                SUM(CASE WHEN c.status = 'AVAILABLE' THEN 1 ELSE 0 END) as available,
                                SUM(CASE WHEN c.status = 'OCCUPIED' THEN 1 ELSE 0 END) as occupied,
                                SUM(CASE WHEN c.status = 'MAINTENANCE' THEN 1 ELSE 0 END) as maintenance,
                                SUM(CASE WHEN c.status = 'CLOSED' THEN 1 ELSE 0 END) as closed,
                                AVG(c.rating) as avgRating,
                                AVG(c.basePrice) as avgPrice
                            FROM Court c
                            WHERE c.deletedAt IS NULL
                        """)
        Object[] getCourtStatistics();

        // === LOCATION QUERIES ===

        @Query("SELECT DISTINCT c.location FROM Court c WHERE c.deletedAt IS NULL ORDER BY c.location")
        List<String> findAllLocations();

        @Query("SELECT c FROM Court c WHERE c.location = :location AND c.deletedAt IS NULL")
        List<Court> findByLocation(@Param("location") String location);

        @Query("SELECT c FROM Court c WHERE c.location = :location AND c.deletedAt IS NULL")
        Page<Court> findByLocation(@Param("location") String location, Pageable pageable);

        // === PRICE QUERIES ===

        @Query("""
                            SELECT c FROM Court c
                            WHERE c.basePrice BETWEEN :minPrice AND :maxPrice
                            AND c.deletedAt IS NULL
                            ORDER BY c.basePrice ASC
                        """)
        List<Court> findByPriceRange(
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice);

        @Query("SELECT MIN(c.basePrice) FROM Court c WHERE c.deletedAt IS NULL")
        Double findMinPrice();

        @Query("SELECT MAX(c.basePrice) FROM Court c WHERE c.deletedAt IS NULL")
        Double findMaxPrice();

        // === AMENITY QUERIES ===

        @Query("""
                            SELECT c FROM Court c
                            JOIN c.amenities a
                            WHERE a.id IN :amenityIds
                            AND c.status = 'AVAILABLE'
                            AND c.deletedAt IS NULL
                            GROUP BY c.id
                            HAVING COUNT(DISTINCT a.id) = :amenityCount
                        """)
        List<Court> findByAmenities(
                        @Param("amenityIds") List<Long> amenityIds,
                        @Param("amenityCount") Long amenityCount);

        // === MAINTENANCE QUERIES ===

        @Query("""
                            SELECT c FROM Court c
                            WHERE c.id IN (
                                SELECT m.court.id FROM Maintenance m
                                WHERE m.status = 'SCHEDULED'
                                AND m.scheduledDate = :date
                            )
                            AND c.deletedAt IS NULL
                        """)
        List<Court> findCourtsWithMaintenanceOnDate(@Param("date") LocalDate date);

        @Query("""
                            SELECT c FROM Court c
                            WHERE c.status = 'MAINTENANCE'
                            AND c.deletedAt IS NULL
                        """)
        List<Court> findCourtsUnderMaintenance();

        // === VALIDATION QUERIES ===

        boolean existsByNameAndDeletedAtIsNull(String name);

        boolean existsByCourtNumberAndDeletedAtIsNull(Integer courtNumber);

        @Query("""
                            SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
                            FROM Court c
                            WHERE c.name = :name
                            AND c.id != :courtId
                            AND c.deletedAt IS NULL
                        """)
        boolean existsByNameAndIdNot(@Param("name") String name, @Param("courtId") Long courtId);
}