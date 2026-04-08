package com.madgarage.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AdminAnalyticsResponse {
    private long totalUsers;
    private long totalSellers;
    private long totalProducts;
    private long totalVehicles;
    private double totalRevenue;
    private List<Double> sixMonthRevenue;
}
