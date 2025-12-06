package com.example.springai.controller;

import com.example.springai.dto.CompatibilityRequest;
import com.example.springai.dto.CompatibilityResponse;
import com.example.springai.service.CompatibilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/compatibility")
@RequiredArgsConstructor
public class CompatibilityController {

    private final CompatibilityService compatibilityService;

    @PostMapping
    public CompatibilityResponse analyzeCompatibility(@RequestBody CompatibilityRequest request) {
        log.info("=== Compatibility API Request ===");
        log.info("Person 1 - Name: {}, Gender: {}, Birth Date: {}",
            request.getName1(), request.getGender1(), request.getBirthDate1());
        log.info("Person 2 - Name: {}, Gender: {}, Birth Date: {}",
            request.getName2(), request.getGender2(), request.getBirthDate2());
        log.info("Compatibility Type: {}", request.getCompatibilityType());
        log.info("=================================");

        CompatibilityResponse response = compatibilityService.analyzeCompatibility(request);

        log.info("=== Compatibility API Response ===");
        log.info("Compatibility Score: {}", response.getCompatibilityScore());
        log.info("Compatibility Type: {}", response.getCompatibilityType());
        log.info("Interpretation length: {} characters", response.getInterpretation().length());
        log.info("==================================");

        return response;
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
