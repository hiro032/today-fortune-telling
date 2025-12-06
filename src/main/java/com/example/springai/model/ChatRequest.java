package com.example.springai.model;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private boolean useRag = false;
    private boolean useAgent = false;
}
