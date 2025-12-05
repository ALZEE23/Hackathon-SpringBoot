package com.niagapulse.model;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "vendor_location_logs")
public class VendorLocationLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vendorId;

    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point location;

    private String weatherCondition;

    private LocalDateTime recordedAt;

    @PrePersist
    protected void onCreate() {
        this.recordedAt = LocalDateTime.now();
    }
}
