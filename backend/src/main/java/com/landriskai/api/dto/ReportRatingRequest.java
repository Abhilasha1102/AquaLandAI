package com.landriskai.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRatingRequest {
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be 1-5")
    @Max(value = 5, message = "Rating must be 1-5")
    private Integer rating;

    @Size(max = 1000, message = "Feedback must be max 1000 characters")
    private String feedback;
}
