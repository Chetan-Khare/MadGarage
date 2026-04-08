package com.madgarage.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRatingRequest {
    @NotNull
    @Min(1)
    @Max(5)
    private Integer partRating;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer deliveryRating;

    private String comment;
}
