package com.example.samplegitflowspringboot.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;

@RestController
public class GreetingController {
    
    @Value("${app.version:unknown}")
    private String appVersion;
    
    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "version", appVersion,
            "environment", System.getProperty("spring.profiles.active", "default")
        );
    }
    
    @GetMapping("/api/greeting")
    public Map<String, String> greeting(@RequestParam(defaultValue = "World") String name) {
        return Map.of("message", "Hello, " + name + "!");
    }
}