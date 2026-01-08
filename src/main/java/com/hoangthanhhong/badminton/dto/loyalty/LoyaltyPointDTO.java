
package com.hoangthanhhong.badminton.dto.loyalty;

import com.hoangthanhhong.badminton.enums.PointTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyPointDTO {
    private Long id;
    private Long userId;
    private String userName;
    private PointTransactionType transactionType;
    private Integer points;
    private Integer balanceAfter;
    private String description;
    private String reference;
    private String relatedEntityType;
    private Long relatedEntityId;
    private LocalDateTime expiresAt;
    private Boolean isExpired;
    private Boolean isReversed;
    private LocalDateTime createdAt;
}
