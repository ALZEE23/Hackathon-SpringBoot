package com.niagapulse.service;

import com.niagapulse.dto.RouteAdviceResponse;
import com.niagapulse.model.VendorLocationLog;
import com.niagapulse.repository.VendorLocationLogRepository;
import com.niagapulse.repository.SalesTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class RouteService {

    @Autowired
    private VendorLocationLogRepository locationRepo;
    @Autowired
    private SalesTransactionRepository salesRepo; // Kita butuh data penjualan historis
    @Autowired
    private IntelligenceService intelligenceService;

    public RouteAdviceResponse getRouteAdvice(String vendorId) {
        
        // 1. AMBIL LOKASI DAN CUACA TERAKHIR
        Optional<VendorLocationLog> lastLog = locationRepo.findFirstByVendorIdOrderByRecordedAtDesc(vendorId);

        if (lastLog.isEmpty()) {
            return createErrorResponse(vendorId, "Data lokasi tidak ditemukan. Nyalakan Tracking HP.");
        }
        
        VendorLocationLog currentLog = lastLog.get();
        
        // 2. AMBIL DATA HISTORIS PENJUALAN (Disederhanakan untuk prompt AI)
        // Kita hitung total counter++ per hari.
        // Nanti lu bisa bikin Query yang lebih canggih di SalesRepo (misal: "3 hari terakhir")
        String historicalData = getSimpleSalesHistory(vendorId); 

        // 3. PANGGIL AI (Kirim data mentah)
        String recommendation = intelligenceService.getRecommendation(currentLog, historicalData);

        // 4. RAKIT JAWABAN (DTO)
        RouteAdviceResponse response = new RouteAdviceResponse();
        response.setVendorId(vendorId);
        response.setCurrentArea(intelligenceService.getAddressName(currentLog.getLocation().getY(), currentLog.getLocation().getX()));
        response.setCurrentWeather(currentLog.getWeatherCondition());
        response.setAiRecommendation(recommendation);
        response.setAiModelUsed(intelligenceService.aiModel);

        return response;
    }
    
    // METHOD BANTU: Ambil data penjualan (simpel, cuma count)
    private String getSimpleSalesHistory(String vendorId) {
        long totalSales = salesRepo.countByVendorId(vendorId);
        // Nanti lu bisa ganti ini dengan query PostGIS yang lebih canggih!
        return String.format("Total penjualan hari ini: %d hits.", totalSales);
    }
    
    private RouteAdviceResponse createErrorResponse(String vendorId, String message) {
         RouteAdviceResponse response = new RouteAdviceResponse();
         response.setVendorId(vendorId);
         response.setAiRecommendation(message);
         return response;
    }
}