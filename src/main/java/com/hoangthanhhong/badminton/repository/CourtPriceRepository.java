package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.CourtPrice;
import com.hoangthanhhong.badminton.enums.DayType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourtPriceRepository extends JpaRepository<CourtPrice, Long> {

    Optional<CourtPrice> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT cp FROM CourtPrice cp WHERE cp.court.id = :courtId AND cp.deletedAt IS NULL ORDER BY cp.dayType, cp.timeStart")
    List<CourtPrice> findByCourtId(@Param("courtId") Long courtId);

    @Query("""
                SELECT cp FROM CourtPrice cp
                WHERE cp.court.id = :courtId
                AND cp.dayType = :dayType
                AND cp.isActive = true
                AND cp.deletedAt IS NULL
                ORDER BY cp.timeStart
            """)
    List<CourtPrice> findByCourtIdAndDayType(
            @Param("courtId") Long courtId,
            @Param("dayType") DayType dayType);

    @Query("""
                SELECT cp FROM CourtPrice cp
                WHERE cp.court.id = :courtId
                AND cp.dayType = :dayType
                AND cp.timeStart <= :time
                AND cp.timeEnd > :time
                AND cp.isActive = true
                AND cp.deletedAt IS NULL
            """)
    Optional<CourtPrice> findPriceForTime(
            @Param("courtId") Long courtId,
            @Param("dayType") DayType dayType,
            @Param("time") LocalTime time);

    @Query("""
                SELECT cp FROM CourtPrice cp
                WHERE cp.court.id = :courtId
                AND cp.dayType = :dayType
                AND (
                    (cp.timeStart < :endTime AND cp.timeEnd > :startTime)
                )
                AND cp.isActive = true
                AND cp.deletedAt IS NULL
                ORDER BY cp.timeStart
            """)
    List<CourtPrice> findPricesForTimeRange(
            @Param("courtId") Long courtId,
            @Param("dayType") DayType dayType,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    @Query("""
                SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END
                FROM CourtPrice cp
                WHERE cp.court.id = :courtId
                AND cp.dayType = :dayType
                AND (
                    (cp.timeStart < :endTime AND cp.timeEnd > :startTime)
                )
                AND cp.id != :priceId
                AND cp.deletedAt IS NULL
            """)
    boolean hasOverlappingPrice(
            @Param("courtId") Long courtId,
            @Param("dayType") DayType dayType,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("priceId") Long priceId);

    @Query("SELECT cp FROM CourtPrice cp WHERE cp.isPeakHour = true AND cp.deletedAt IS NULL")
    List<CourtPrice> findPeakHourPrices();

    @Query("SELECT COUNT(cp) FROM CourtPrice cp WHERE cp.court.id = :courtId AND cp.deletedAt IS NULL")
    Long countByCourtId(@Param("courtId") Long courtId);
}