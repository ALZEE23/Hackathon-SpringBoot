package com.niagapulse.controller;

import com.niagapulse.dto.TrackingRequest;
import com.niagapulse.service.TrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    private static final Logger log = LoggerFactory.getLogger(TrackingController.class);
    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @PostMapping("/update")
    public ResponseEntity<String> updatePosition(@RequestBody TrackingRequest request) {
        try {
            if (request == null || request.getLatitude() == null || request.getLongitude() == null) {
                log.warn("Invalid tracking request: missing required fields");
                return ResponseEntity.badRequest().body("Error: latitude and longitude are required");
            }

            trackingService.logLocation(request);
            log.info("Location update successful for vendor: {}", request.getVendorId());
            return ResponseEntity.ok("Location received and logged");
        } catch (Exception e) {
            log.error("Error updating location: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}