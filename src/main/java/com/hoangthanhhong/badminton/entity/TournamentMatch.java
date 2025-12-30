package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tournament_matches", indexes = {
        @Index(name = "idx_tournament_id", columnList = "tournament_id"),
        @Index(name = "idx_round_id", columnList = "round_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_scheduled_time", columnList = "scheduled_time")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentMatch extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id")
    private TournamentRound round;

    @Column(name = "match_number")
    private Integer matchNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant1_id", nullable = false)
    private TournamentParticipant participant1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant2_id")
    private TournamentParticipant participant2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private TournamentParticipant winner;

    @Column(name = "participant1_score")
    private Integer participant1Score;

    @Column(name = "participant2_score")
    private Integer participant2Score;

    @Column(name = "participant1_set1")
    private Integer participant1Set1;

    @Column(name = "participant1_set2")
    private Integer participant1Set2;

    @Column(name = "participant1_set3")
    private Integer participant1Set3;

    @Column(name = "participant2_set1")
    private Integer participant2Set1;

    @Column(name = "participant2_set2")
    private Integer participant2Set2;

    @Column(name = "participant2_set3")
    private Integer participant2Set3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MatchStatus status = MatchStatus.SCHEDULED;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id")
    private Court court;

    @Column(name = "referee_id")
    private Long refereeId;

    @Column(name = "referee_name", length = 100)
    private String refereeName;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "live_stream_url", length = 500)
    private String liveStreamUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_walkover")
    @Builder.Default
    private Boolean isWalkover = false;

    @Column(name = "walkover_reason", columnDefinition = "TEXT")
    private String walkoverReason;

    // === HELPER METHODS ===

    public void start() {
        this.status = MatchStatus.IN_PROGRESS;
        this.actualStartTime = LocalDateTime.now();
    }

    public void complete(TournamentParticipant winner) {
        this.status = MatchStatus.COMPLETED;
        this.winner = winner;
        this.actualEndTime = LocalDateTime.now();

        // Update participant statistics
        if (participant1 != null && participant2 != null) {
            boolean p1Won = winner.getId().equals(participant1.getId());
            participant1.recordMatchResult(p1Won, participant1Score, participant2Score);
            participant2.recordMatchResult(!p1Won, participant2Score, participant1Score);
        }
    }

    public void cancel(String reason) {
        this.status = MatchStatus.CANCELLED;
        this.notes = reason;
    }

    public void postpone(LocalDateTime newTime) {
        this.status = MatchStatus.POSTPONED;
        this.scheduledTime = newTime;
    }

    public void declareWalkover(TournamentParticipant winner, String reason) {
        this.isWalkover = true;
        this.walkoverReason = reason;
        this.winner = winner;
        this.status = MatchStatus.COMPLETED;
        this.actualEndTime = LocalDateTime.now();
    }

    public Integer getDuration() {
        if (actualStartTime != null && actualEndTime != null) {
            return (int) java.time.Duration.between(actualStartTime, actualEndTime).toMinutes();
        }
        return null;
    }

    public String getScoreDisplay() {
        if (participant1Score == null || participant2Score == null) {
            return "Not played";
        }
        return String.format("%d - %d", participant1Score, participant2Score);
    }

    public String getDetailedScore() {
        if (participant1Set1 == null)
            return getScoreDisplay();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Set 1: %d-%d", participant1Set1, participant2Set1));
        if (participant1Set2 != null) {
            sb.append(String.format(" | Set 2: %d-%d", participant1Set2, participant2Set2));
        }
        if (participant1Set3 != null) {
            sb.append(String.format(" | Set 3: %d-%d", participant1Set3, participant2Set3));
        }
        return sb.toString();
    }
}
