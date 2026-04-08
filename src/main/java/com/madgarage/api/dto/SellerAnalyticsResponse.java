package com.madgarage.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerAnalyticsResponse {
    private long activeListings;
    private double monthRevenue;
}
