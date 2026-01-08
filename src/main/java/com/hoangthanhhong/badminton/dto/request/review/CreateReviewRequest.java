package com.hoangthanhhong.badminton.dto.request.review;

import com.hoangthanhhong.badminton.enums.ReviewType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewRequest {

    @NotNull(message = "Review type is required")
    private ReviewType reviewType;

    private Long courtId;
    private Long productId;
    private Long bookingId;
    private Long orderId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;

    // ✅ THÊM CÁC FIELD NÀY
    @Min(value = 1)
    @Max(value = 5)
    private Integer qualityRating;

    @Min(value = 1)
    @Max(value = 5)
    private Integer serviceRating;

    @Min(value = 1)
    @Max(value = 5)
    private Integer cleanlinessRating;

    @Min(value = 1)
    @Max(value = 5)
    private Integer valueRating;

    @Min(value = 1)
    @Max(value = 5)
    private Integer locationRating;

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Comment must not exceed 2000 characters")
    private String comment;

    private List<String> images;
    private List<String> videos; // ✅ THÊM FIELD NÀY
}
