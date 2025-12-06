package com.example.springai.dto;

import lombok.Data;

@Data
public class CompatibilityRequest {
    // 첫 번째 사람
    private String name1;
    private String gender1;
    private String birthDate1;  // YYYY-MM-DD

    // 두 번째 사람
    private String name2;
    private String gender2;
    private String birthDate2;  // YYYY-MM-DD

    // 궁합 타입 (optional)
    private String compatibilityType; // "love", "business", "friendship", "marriage"
}
