package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.TournamentStatus;
import com.hoangthanhhong.badminton.enums.TournamentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournaments", indexes = {
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_dates", columnList = "start_date, end_date"),
        @Index(name = "idx_court_id", columnList = "court_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tournament extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id")
    private Court court;

    @Enumerated(EnumType.STRING)
    @Column(name = "tournament_type", nullable = false, length = 20)
    @Builder.Default
    private TournamentType tournamentType = TournamentType.SINGLES;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "registration_start_date")
    private LocalDate registrationStartDate;

    @Column(name = "registration_end_date")
    private LocalDate registrationEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TournamentStatus status = TournamentStatus.UPCOMING;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(name = "current_participants")
    @Builder.Default
    private Integer currentParticipants = 0;

    @Column(name = "min_participants")
    private Integer minParticipants;

    @Column(name = "entry_fee")
    private Double entryFee;

    @Column(name = "prize_pool")
    private Double prizePool;

    @Column(name = "first_prize")
    private Double firstPrize;

    @Column(name = "second_prize")
    private Double secondPrize;

    @Column(name = "third_prize")
    private Double thirdPrize;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "banner_url", length = 500)
    private String bannerUrl;

    @Column(name = "rules", columnDefinition = "TEXT")
    private String rules;

    @Column(name = "format", length = 100)
    private String format; // SINGLE_ELIMINATION, DOUBLE_ELIMINATION, ROUND_ROBIN

    @Column(name = "age_restriction")
    private String ageRestriction; // "18+", "Under 21", etc.

    @Column(name = "skill_level")
    private String skillLevel; // BEGINNER, INTERMEDIATE, ADVANCED, PROFESSIONAL

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = true;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "organizer_id")
    private Long organizerId;

    @Column(name = "organizer_name", length = 200)
    private String organizerName;

    @Column(name = "sponsor", length = 200)
    private String sponsor;

    @Column(name = "location", length = 500)
    private String location;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    // === RELATIONSHIPS ===

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TournamentParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TournamentMatch> matches = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TournamentRound> rounds = new ArrayList<>();

    @OneToOne(mappedBy = "relatedTournament", cascade = CascadeType.ALL)
    private ChatRoom chatRoom;

    // === HELPER METHODS ===

    public boolean isRegistrationOpen() {
        LocalDate now = LocalDate.now();
        return status == TournamentStatus.UPCOMING
                && (registrationStartDate == null || !now.isBefore(registrationStartDate))
                && (registrationEndDate == null || !now.isAfter(registrationEndDate))
                && currentParticipants < maxParticipants;
    }

    public boolean isFull() {
        return currentParticipants >= maxParticipants;
    }

    public boolean canStart() {
        return status == TournamentStatus.UPCOMING
                && currentParticipants >= (minParticipants != null ? minParticipants : 2)
                && !LocalDate.now().isBefore(startDate);
    }

    public void start() {
        this.status = TournamentStatus.ONGOING;
        this.startedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = TournamentStatus.COMPLETED;
        this.endedAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        this.status = TournamentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }

    public void incrementParticipants() {
        this.currentParticipants++;
    }

    public void decrementParticipants() {
        if (this.currentParticipants > 0) {
            this.currentParticipants--;
        }
    }

    public Integer getAvailableSlots() {
        return maxParticipants - currentParticipants;
    }

    public Double getTotalPrizePool() {
        if (prizePool != null)
            return prizePool;

        Double total = 0.0;
        if (firstPrize != null)
            total += firstPrize;
        if (secondPrize != null)
            total += secondPrize;
        if (thirdPrize != null)
            total += thirdPrize;

        return total;
    }

    public boolean isActive() {
        return status == TournamentStatus.ONGOING;
    }

    public boolean isUpcoming() {
        return status == TournamentStatus.UPCOMING;
    }

    public boolean isCompleted() {
        return status == TournamentStatus.COMPLETED;
    }
}
