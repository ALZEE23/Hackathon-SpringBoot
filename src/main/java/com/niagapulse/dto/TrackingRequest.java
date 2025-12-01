package com.niagapulse.dto;

import lombok.Data;

@Data // Lombok otomatis bikinin Getter & Setter (getLatitude, setLatitude, dll)
public class TrackingRequest {
    
    // Siapa yang ngirim?
    private String vendorId;
    
    // Koordinat Latitude (Garis Lintang - Y)
    // Pake Double karena ada komanya (contoh: -6.914744)
    private Double latitude;
    
    // Koordinat Longitude (Garis Bujur - X)
    // Pake Double karena ada komanya (contoh: 107.609810)
    private Double longitude;
}