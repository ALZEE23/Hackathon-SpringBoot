package com.niagapulse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niagapulse.model.VendorLocationLog;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntelligenceServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private IntelligenceService intelligenceService;

    private VendorLocationLog validLog;

    @BeforeEach
    void setUp() {
        // Create a valid location log
        validLog = new VendorLocationLog();
        validLog.setVendorId("VENDOR_001");
        
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point location = geometryFactory.createPoint(new Coordinate(106.8272, -6.1754));
        validLog.setLocation(location);
        validLog.setWeatherCondition("Clear");
        
        // Set API keys via reflection
        ReflectionTestUtils.setField(intelligenceService, "weatherApiKey", "test-weather-key");
        ReflectionTestUtils.setField(intelligenceService, "aiApiKey", "test-ai-key");
        ReflectionTestUtils.setField(intelligenceService, "aiModel", "Claude Sonnet 4.5");
        ReflectionTestUtils.setField(intelligenceService, "AI_ENDPOINT", "https://api.openai.com/v1/chat/completions");
    }

    @Test
    void testGetWeather_WithValidCoordinates() throws Exception {
        // Arrange
        String mockWeatherResponse = "{\"weather\":[{\"main\":\"Clear\",\"description\":\"clear sky\"}]}";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockWeatherResponse, HttpStatus.OK));
        
        ObjectMapper realMapper = new ObjectMapper();
        ReflectionTestUtils.setField(intelligenceService, "objectMapper", realMapper);

        // Act
        String result = intelligenceService.getWeather(-6.1754, 106.8272);

        // Assert
        assertNotNull(result);
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(String.class));
    }

    @Test
    void testGetWeather_WithNullLatitude() {
        // Act
        String result = intelligenceService.getWeather(null, 106.8272);

        // Assert
        assertEquals("Unknown", result);
        verify(restTemplate, never()).getForEntity(anyString(), eq(String.class));
    }

    @Test
    void testGetWeather_WithNullLongitude() {
        // Act
        String result = intelligenceService.getWeather(-6.1754, null);

        // Assert
        assertEquals("Unknown", result);
        verify(restTemplate, never()).getForEntity(anyString(), eq(String.class));
    }

    @Test
    void testGetAddressName_WithValidCoordinates() throws Exception {
        // Arrange
        String mockGeoResponse = "{\"display_name\":\"Jakarta, Indonesia\"}";
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockGeoResponse, HttpStatus.OK));
        
        ObjectMapper realMapper = new ObjectMapper();
        ReflectionTestUtils.setField(intelligenceService, "objectMapper", realMapper);

        // Act
        String result = intelligenceService.getAddressName(-6.1754, 106.8272);

        // Assert
        assertNotNull(result);
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(String.class));
    }

    @Test
    void testGetAddressName_WithNullCoordinates() {
        // Act
        String result = intelligenceService.getAddressName(null, null);

        // Assert
        assertEquals("Area Tidak Dikenal", result);
    }

    @Test
    void testGetRecommendation_WithValidLog() throws Exception {
        // Arrange
        String mockGeoResponse = "{\"display_name\":\"Jakarta\"}";
        String mockAiResponse = "{\"choices\":[{\"message\":{\"content\":\"Pindah ke utara\"}}]}";
        
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockGeoResponse, HttpStatus.OK));
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockAiResponse, HttpStatus.OK));
        
        ObjectMapper realMapper = new ObjectMapper();
        ReflectionTestUtils.setField(intelligenceService, "objectMapper", realMapper);

        // Act
        String result = intelligenceService.getRecommendation(validLog, "Penjualan minggu lalu: 5 juta");

        // Assert
        assertNotNull(result);
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(String.class));
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    void testGetRecommendation_WithNullLog() {
        // Act
        String result = intelligenceService.getRecommendation(null, "Penjualan minggu lalu: 5 juta");

        // Assert
        assertEquals("Tidak ada rekomendasi (lokasi tidak tersedia)", result);
    }
}
