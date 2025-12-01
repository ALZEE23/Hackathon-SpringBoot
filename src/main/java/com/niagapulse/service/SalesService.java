package com.niagapulse.service;

import com.niagapulse.dto.SalesRequest;
import com.niagapulse.model.SalesTransaction;
import com.niagapulse.model.VendorLocationLog;
import com.niagapulse.repository.SalesTransactionRepository;
import com.niagapulse.repository.VendorLocationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class SalesService {

    @Autowired
    private SalesTransactionRepository salesRepo;

    @Autowired
    private VendorLocationLogRepository locationRepo; 

    public void recordSale(SalesRequest request) {
        SalesTransaction trx = new SalesTransaction();
        trx.setVendorId(request.getVendorId());

        // DETEKTIF: Cari lokasi terakhir pedagang
        Optional<VendorLocationLog> lastKnown = locationRepo.findFirstByVendorIdOrderByRecordedAtDesc(request.getVendorId());

        if (lastKnown.isPresent()) {
            trx.setLocation(lastKnown.get().getLocation());
            System.out.println("✅ Sales +1 (Auto-Locate Success)");
        } else {
            System.out.println("⚠️ Sales +1 (No Location Data - Pedagang Gak Tracking)");
        }

        salesRepo.save(trx);
    }
}