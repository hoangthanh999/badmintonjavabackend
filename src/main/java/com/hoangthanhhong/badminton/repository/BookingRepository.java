// File: BookingRepository.java (NẾU CHƯA CÓ)
package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.Booking;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    long countByCourt_IdAndDate(Long courtId, LocalDate date);

}
