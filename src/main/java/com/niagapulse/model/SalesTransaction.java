package com.niagapulse.model;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "sales_transactions")
public class SalesTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vendorId; // Siapa pedagangnya?

    // Lokasi ditemuakan otomatis oleh sistem
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    private LocalDateTime transactionTime;

    @PrePersist
    protected void onCreate() {
        transactionTime = LocalDateTime.now();
    }
}