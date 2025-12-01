package com.niagapulse.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello World");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/hello/{name}")
    public ResponseEntity<Map<String, String>> greetByName(@PathVariable String name) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello " + name);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
