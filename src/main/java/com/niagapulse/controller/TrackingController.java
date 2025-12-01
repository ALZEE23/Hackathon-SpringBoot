package com.niagapulse.controller;

import com.niagapulse.dto.TrackingRequest;
import com.niagapulse.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping; // WAJIB
import org.springframework.web.bind.annotation.RequestBody; // WAJIB
import org.springframework.web.bind.annotation.RequestMapping; // WAJIB
import org.springframework.web.bind.annotation.RestController; // WAJIB

@RestController // PENTING: Spring tahu ini API, bukan halaman web
@RequestMapping("/api/tracking") // Basis URL-nya
public class TrackingController {

    @Autowired
    private TrackingService trackingService;

    // Endpoint: POST http://localhost:8080/api/tracking/update
    @PostMapping("/update") // Method POST untuk URL /update
    public ResponseEntity<String> updatePosition(@RequestBody TrackingRequest request) {
        
        trackingService.logLocation(request);
        
        return ResponseEntity.ok("Lokasi diterima. Aman.");
    }
}