package com.hoangthanhhong.badminton.dto.request.review;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReviewRequest {
    private String title;
    private String comment;
    private Integer rating;
}
