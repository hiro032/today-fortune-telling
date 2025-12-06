package com.example.springai.controller;

import com.example.springai.dto.SajuRequest;
import com.example.springai.dto.SajuResponse;
import com.example.springai.service.SajuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saju")
@RequiredArgsConstructor
public class SajuController {

    private final SajuService sajuService;

    @PostMapping
    public SajuResponse analyzeSaju(@RequestBody SajuRequest request) {
        return sajuService.analyzeSaju(request);
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
