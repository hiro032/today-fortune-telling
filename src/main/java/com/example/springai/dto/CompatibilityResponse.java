package com.example.springai.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompatibilityResponse {
    private String name1;
    private String name2;
    private String birthDate1;
    private String birthDate2;

    // 궁합 점수 (0-100)
    private int compatibilityScore;

    // 각 사람의 사주
    private String saju1;
    private String saju2;

    // 궁합 타입
    private String compatibilityType;

    // AI 궁합 해석
    private String interpretation;

    // 추천 사항
    private String recommendation;
}
