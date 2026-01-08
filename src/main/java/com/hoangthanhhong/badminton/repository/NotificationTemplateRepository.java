package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.NotificationTemplate;
import com.hoangthanhhong.badminton.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    Optional<NotificationTemplate> findByCode(String code);

    List<NotificationTemplate> findByType(NotificationType type);

    @Query("""
                SELECT nt FROM NotificationTemplate nt
                WHERE nt.isActive = true
                ORDER BY nt.type, nt.name
            """)
    List<NotificationTemplate> findAllActive();

    boolean existsByCode(String code);
}
