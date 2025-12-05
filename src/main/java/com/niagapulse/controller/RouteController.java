package com.niagapulse.controller;

import com.niagapulse.dto.RouteAdviceResponse;
import com.niagapulse.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    // Endpoint: GET http://localhost:8080/api/route/advice?vendorId=PedagangSateA
    // Gunakan GET karena kita MEMINTA saran, bukan MENGIRIM data baru
    @GetMapping("/advice")
    public ResponseEntity<RouteAdviceResponse> getAdvice(@RequestParam String vendorId) {
        
        RouteAdviceResponse advice = routeService.getRouteAdvice(vendorId);
        
        return ResponseEntity.ok(advice);
    }
}