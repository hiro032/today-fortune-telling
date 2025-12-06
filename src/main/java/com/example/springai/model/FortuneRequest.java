package com.example.springai.model;

import lombok.Data;

@Data
public class FortuneRequest {
    private String name;
    private String gender;
    private String birthDate; // YYYY-MM-DD format
}
