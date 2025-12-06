package com.example.springai.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SajuResponse {
    private String name;
    private String birthDate;
    private String birthTime;
    private String yearPillar;    // 년주
    private String monthPillar;   // 월주
    private String dayPillar;     // 일주
    private String hourPillar;    // 시주
    private String saju;          // 전체 사주팔자
    private String interpretation; // AI 해석
}
