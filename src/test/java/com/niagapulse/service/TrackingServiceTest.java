package com.niagapulse.service;

import com.niagapulse.dto.TrackingRequest;
import com.niagapulse.model.VendorLocationLog;
import com.niagapulse.repository.VendorLocationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackingServiceTest {

    @Mock
    private VendorLocationLogRepository locationRepo;

    @Mock
    private IntelligenceService intelligenceService;

    @InjectMocks
    private TrackingService trackingService;

    private TrackingRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new TrackingRequest();
        validRequest.setVendorId("VENDOR_001");
        validRequest.setLatitude(-6.1754);
        validRequest.setLongitude(106.8272);
    }

    @Test
    void testLogLocation_Success() {
        // Arrange
        when(intelligenceService.getWeather(-6.1754, 106.8272)).thenReturn("Clear");
        when(locationRepo.save(any(VendorLocationLog.class))).thenReturn(new VendorLocationLog());

        // Act
        trackingService.logLocation(validRequest);

        // Assert
        verify(intelligenceService, times(1)).getWeather(-6.1754, 106.8272);
        verify(locationRepo, times(1)).save(any(VendorLocationLog.class));
    }

    @Test
    void testLogLocation_WithNullRequest() {
        // Act & Assert
        trackingService.logLocation(null);

        // Verify no database calls were made
        verify(locationRepo, never()).save(any());
        verify(intelligenceService, never()).getWeather(anyDouble(), anyDouble());
    }

    @Test
    void testLogLocation_WithNullLatitude() {
        // Arrange
        validRequest.setLatitude(null);

        // Act & Assert
        trackingService.logLocation(validRequest);

        // Verify no database calls were made
        verify(locationRepo, never()).save(any());
    }

    @Test
    void testLogLocation_WithNullLongitude() {
        // Arrange
        validRequest.setLongitude(null);

        // Act & Assert
        trackingService.logLocation(validRequest);

        // Verify no database calls were made
        verify(locationRepo, never()).save(any());
    }

    @Test
    void testLogLocation_ValidatesWeatherIntegration() {
        // Arrange
        String expectedWeather = "Rainy";
        when(intelligenceService.getWeather(validRequest.getLatitude(), validRequest.getLongitude()))
                .thenReturn(expectedWeather);
        when(locationRepo.save(any(VendorLocationLog.class))).thenAnswer(invocation -> {
            VendorLocationLog log = invocation.getArgument(0);
            assert log.getWeatherCondition().equals(expectedWeather);
            return log;
        });

        // Act
        trackingService.logLocation(validRequest);

        // Assert
        verify(locationRepo, times(1)).save(any(VendorLocationLog.class));
    }
}
