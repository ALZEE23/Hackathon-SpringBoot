package com.niagapulse.controller;

import com.niagapulse.dto.TrackingRequest;
import com.niagapulse.service.TrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackingControllerTest {

    @Mock
    private TrackingService trackingService;

    @InjectMocks
    private TrackingController trackingController;

    private TrackingRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new TrackingRequest();
        validRequest.setVendorId("VENDOR_001");
        validRequest.setLatitude(-6.1754);
        validRequest.setLongitude(106.8272);
    }

    @Test
    void testUpdatePosition_WithValidRequest() {
        // Arrange
        doNothing().when(trackingService).logLocation(validRequest);

        // Act
        ResponseEntity<String> response = trackingController.updatePosition(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Location received"));
        verify(trackingService, times(1)).logLocation(validRequest);
    }

    @Test
    void testUpdatePosition_WithNullRequest() {
        // Act
        ResponseEntity<String> response = trackingController.updatePosition(null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("required"));
    }

    @Test
    void testUpdatePosition_WithMissingLatitude() {
        // Arrange
        validRequest.setLatitude(null);

        // Act
        ResponseEntity<String> response = trackingController.updatePosition(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(trackingService, never()).logLocation(any());
    }

    @Test
    void testUpdatePosition_WithMissingLongitude() {
        // Arrange
        validRequest.setLongitude(null);

        // Act
        ResponseEntity<String> response = trackingController.updatePosition(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(trackingService, never()).logLocation(any());
    }

    @Test
    void testUpdatePosition_ServiceException() {
        // Arrange
        doThrow(new RuntimeException("Database error")).when(trackingService).logLocation(any());

        // Act
        ResponseEntity<String> response = trackingController.updatePosition(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error"));
    }
}
