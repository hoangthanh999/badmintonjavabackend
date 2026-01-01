package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    Optional<Shift> findByName(String name);

    List<Shift> findByIsActive(Boolean isActive);

    @Query("""
                SELECT s FROM Shift s
                WHERE s.isActive = true
                ORDER BY s.startTime ASC
            """)
    List<Shift> findAllActiveShifts();

    @Query("""
                SELECT s FROM Shift s
                WHERE s.isDefault = true
            """)
    Optional<Shift> findDefaultShift();
}
