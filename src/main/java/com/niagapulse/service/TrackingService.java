package com.niagapulse.service;

import com.niagapulse.dto.TrackingRequest;
import com.niagapulse.model.VendorLocationLog;
import com.niagapulse.repository.VendorLocationLogRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {

    private static final Logger log = LoggerFactory.getLogger(TrackingService.class);
    private final VendorLocationLogRepository locationRepo;
    private final IntelligenceService intelligenceService;
    private final GeometryFactory geometryFactory;

    public TrackingService(VendorLocationLogRepository locationRepo, IntelligenceService intelligenceService) {
        this.locationRepo = locationRepo;
        this.intelligenceService = intelligenceService;
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    }

    public void logLocation(TrackingRequest request) {
        try {
            if (request == null || request.getLatitude() == null || request.getLongitude() == null) {
                log.warn("Invalid request: null values detected");
                return;
            }

            Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
            
            VendorLocationLog vendorLog = new VendorLocationLog();
            vendorLog.setVendorId(request.getVendorId());
            vendorLog.setLocation(point);
            
            // Fetch weather asynchronously (or with timeout) to avoid blocking
            String currentWeather = intelligenceService.getWeather(request.getLatitude(), request.getLongitude());
            vendorLog.setWeatherCondition(currentWeather);

            locationRepo.save(vendorLog);
            log.info("📍 Location logged: lat={}, lon={}, weather={}, vendor={}", 
                    request.getLatitude(), request.getLongitude(), currentWeather, request.getVendorId());
        } catch (Exception e) {
            log.error("Failed to log location: {}", e.getMessage(), e);
            throw new RuntimeException("Location logging failed", e);
        }
    }
}