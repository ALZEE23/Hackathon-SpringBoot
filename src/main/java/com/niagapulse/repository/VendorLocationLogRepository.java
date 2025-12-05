package com.niagapulse.repository;

import com.niagapulse.model.VendorLocationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VendorLocationLogRepository extends JpaRepository<VendorLocationLog, Long> {
    
    // QUERY: Cari lokasi terakhir yang direkam vendor ini
    Optional<VendorLocationLog> findFirstByVendorIdOrderByRecordedAtDesc(String vendorId);
}