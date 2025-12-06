package com.example.springai.dto;

import lombok.Data;

@Data
public class SajuRequest {
    private String name;
    private String gender;
    private String birthDate;  // YYYY-MM-DD
    private String birthTime;  // HH:mm (optional)
}
