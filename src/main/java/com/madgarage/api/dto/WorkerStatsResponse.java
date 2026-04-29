package com.madgarage.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerStatsResponse {
    private long totalUsers;
    private long totalSellers;
    private long totalProducts;
    private long totalVehicles;
}
