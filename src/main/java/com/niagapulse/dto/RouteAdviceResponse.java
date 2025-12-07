package com.niagapulse.dto;

import lombok.Data;

@Data
public class RouteAdviceResponse {
    private String vendorId;
    private String currentArea;
    private String currentWeather;
    private String aiRecommendation; // Saran dari AI (e.g., "Segera menuju ke arah barat daya...")
    private String aiModelUsed; // Model AI yang digunakan (e.g., gpt-4o-mini)
}