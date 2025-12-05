package com.niagapulse.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

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

    @GetMapping("/hello/user")
    public ResponseEntity<Map<String, String>> greetUser(Authentication authentication) {
        String username = authentication.getName();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello " + username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
