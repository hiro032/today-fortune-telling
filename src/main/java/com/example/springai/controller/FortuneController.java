package com.example.springai.controller;

import com.example.springai.model.FortuneRequest;
import com.example.springai.service.FortuneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/fortune")
@CrossOrigin(origins = "*")
public class FortuneController {

    private final FortuneService fortuneService;

    public FortuneController(FortuneService fortuneService) {
        this.fortuneService = fortuneService;
    }

    /**
     * Get today's fortune
     */
    @PostMapping
    public Map<String, String> getFortune(@RequestBody FortuneRequest request) {
        log.info("=== Fortune API Request ===");
        log.info("Name: {}", request.getName());
        log.info("Gender: {}", request.getGender());
        log.info("Birth Date: {}", request.getBirthDate());
        log.info("===========================");

        String fortune = fortuneService.getTodaysFortune(request);

        Map<String, String> response = new HashMap<>();
        response.put("fortune", fortune);
        response.put("name", request.getName());
        response.put("gender", request.getGender());
        response.put("birthDate", request.getBirthDate());

        log.info("=== Fortune API Response ===");
        log.info("Name: {}", response.get("name"));
        log.info("Fortune length: {} characters", fortune.length());
        log.info("============================");

        return response;
    }
}
