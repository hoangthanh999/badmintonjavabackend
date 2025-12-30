package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournament_rounds", indexes = {
        @Index(name = "idx_tournament_id", columnList = "tournament_id"),
        @Index(name = "idx_round_number", columnList = "round_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentRound extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Column(nullable = false, length = 100)
    private String name; // Round of 16, Quarter-finals, Semi-finals, Final

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "total_matches")
    private Integer totalMatches;

    @Column(name = "completed_matches")
    @Builder.Default
    private Integer completedMatches = 0;

    // === RELATIONSHIPS ===

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("matchNumber ASC")
    @Builder.Default
    private List<TournamentMatch> matches = new ArrayList<>();

    // === HELPER METHODS ===

    public void incrementCompletedMatches() {
        this.completedMatches++;
        if (this.completedMatches.equals(this.totalMatches)) {
            this.isCompleted = true;
        }
    }

    public Integer getRemainingMatches() {
        if (totalMatches == null)
            return null;
        return totalMatches - completedMatches;
    }

    public Double getCompletionPercentage() {
        if (totalMatches == null || totalMatches == 0)
            return 0.0;
        return (double) completedMatches / totalMatches * 100;
    }
}
