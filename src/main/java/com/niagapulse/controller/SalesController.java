package com.niagapulse.controller;

import com.niagapulse.dto.SalesRequest;
import com.niagapulse.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    @Autowired
    private SalesService salesService;

    @PostMapping("/record")
    public ResponseEntity<String> recordTransaction(@RequestBody SalesRequest request) {
        salesService.recordSale(request);
        return ResponseEntity.ok("Counter ++ Success");
    }
}