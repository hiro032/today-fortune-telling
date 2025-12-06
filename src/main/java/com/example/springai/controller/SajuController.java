package com.example.springai.controller;

import com.example.springai.dto.SajuRequest;
import com.example.springai.dto.SajuResponse;
import com.example.springai.service.SajuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/saju")
@RequiredArgsConstructor
public class SajuController {

    private final SajuService sajuService;

    @PostMapping
    public SajuResponse analyzeSaju(@RequestBody SajuRequest request) {
        log.info("=== Saju API Request ===");
        log.info("Name: {}", request.getName());
        log.info("Gender: {}", request.getGender());
        log.info("Birth Date: {}", request.getBirthDate());
        log.info("Birth Time: {}", request.getBirthTime() != null ? request.getBirthTime() : "Not provided");
        log.info("========================");

        SajuResponse response = sajuService.analyzeSaju(request);

        log.info("=== Saju API Response ===");
        log.info("Name: {}", response.getName());
        log.info("Saju: {}", response.getSaju());
        log.info("Year Pillar: {}", response.getYearPillar());
        log.info("Month Pillar: {}", response.getMonthPillar());
        log.info("Day Pillar: {}", response.getDayPillar());
        log.info("Hour Pillar: {}", response.getHourPillar());
        log.info("Interpretation length: {} characters", response.getInterpretation().length());
        log.info("=========================");

        return response;
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
