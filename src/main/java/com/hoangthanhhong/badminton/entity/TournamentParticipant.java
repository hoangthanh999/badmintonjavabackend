package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.ParticipantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tournament_participants", uniqueConstraints = @UniqueConstraint(columnNames = { "tournament_id",
        "user_id" }), indexes = {
                @Index(name = "idx_tournament_id", columnList = "tournament_id"),
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_status", columnList = "status")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentParticipant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private User partner; // For doubles tournaments

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ParticipantStatus status = ParticipantStatus.REGISTERED;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @Column(name = "seed_number")
    private Integer seedNumber; // Seeding for bracket

    @Column(name = "team_name", length = 100)
    private String teamName;

    @Column(name = "jersey_number")
    private Integer jerseyNumber;

    @Column(name = "payment_status", length = 20)
    @Builder.Default
    private String paymentStatus = "PENDING";

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "checked_in")
    @Builder.Default
    private Boolean checkedIn = false;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "final_rank")
    private Integer finalRank;

    @Column(name = "prize_won")
    private Double prizeWon;

    @Column(name = "matches_played")
    @Builder.Default
    private Integer matchesPlayed = 0;

    @Column(name = "matches_won")
    @Builder.Default
    private Integer matchesWon = 0;

    @Column(name = "matches_lost")
    @Builder.Default
    private Integer matchesLost = 0;

    @Column(name = "points_scored")
    @Builder.Default
    private Integer pointsScored = 0;

    @Column(name = "points_conceded")
    @Builder.Default
    private Integer pointsConceded = 0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "withdrawal_reason", columnDefinition = "TEXT")
    private String withdrawalReason;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    // === HELPER METHODS ===

    @PrePersist
    public void setRegisteredAt() {
        if (registeredAt == null) {
            registeredAt = LocalDateTime.now();
        }
    }

    public void checkIn() {
        this.checkedIn = true;
        this.checkInTime = LocalDateTime.now();
        this.status = ParticipantStatus.CONFIRMED;
    }

    public void withdraw(String reason) {
        this.status = ParticipantStatus.WITHDRAWN;
        this.withdrawalReason = reason;
        this.withdrawnAt = LocalDateTime.now();
    }

    public void disqualify(String reason) {
        this.status = ParticipantStatus.DISQUALIFIED;
        this.withdrawalReason = reason;
    }

    public void recordMatchResult(boolean won, Integer pointsFor, Integer pointsAgainst) {
        this.matchesPlayed++;
        if (won) {
            this.matchesWon++;
        } else {
            this.matchesLost++;
        }
        this.pointsScored += pointsFor;
        this.pointsConceded += pointsAgainst;
    }

    public Double getWinRate() {
        if (matchesPlayed == 0)
            return 0.0;
        return (double) matchesWon / matchesPlayed * 100;
    }

    public Integer getPointsDifference() {
        return pointsScored - pointsConceded;
    }

    public boolean isActive() {
        return status == ParticipantStatus.CONFIRMED || status == ParticipantStatus.REGISTERED;
    }
}
