package com.niagapulse.service;

import com.niagapulse.dto.TrackingRequest;
import com.niagapulse.model.VendorLocationLog;
import com.niagapulse.repository.VendorLocationLogRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {

    @Autowired
    private VendorLocationLogRepository locationRepo;

    @Autowired
    private IntelligenceService intelligenceService; // INJECT SERVICE CUACA

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public void logLocation(TrackingRequest request) {
        Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        
        VendorLocationLog log = new VendorLocationLog();
        log.setVendorId(request.getVendorId());
        log.setLocation(point);
        
        // 🔥 PANGGIL CUACA OTOMATIS
        String currentCuaca = intelligenceService.getWeather(request.getLatitude(), request.getLongitude());
        log.setWeatherCondition(currentCuaca);

        locationRepo.save(log);
        
        System.out.println("📍 Jejak: " + request.getLatitude() + "," + request.getLongitude() + " | 🌤️ " + currentCuaca);
    }
}