package com.example.springai.controller;

import com.example.springai.model.FortuneRequest;
import com.example.springai.service.FortuneService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
        String fortune = fortuneService.getTodaysFortune(request);

        Map<String, String> response = new HashMap<>();
        response.put("fortune", fortune);
        response.put("name", request.getName());

        return response;
    }
}
