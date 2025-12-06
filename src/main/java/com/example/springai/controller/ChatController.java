package com.example.springai.controller;

import com.example.springai.model.ChatRequest;
import com.example.springai.model.ChatResponse;
import com.example.springai.service.AgentService;
import com.example.springai.service.ChatService;
import com.example.springai.service.RagService;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final RagService ragService;
    private final AgentService agentService;

    public ChatController(ChatService chatService,
                         RagService ragService,
                         AgentService agentService) {
        this.chatService = chatService;
        this.ragService = ragService;
        this.agentService = agentService;
    }

    /**
     * Unified chat endpoint that supports simple chat, RAG, or Agent mode
     */
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String response;
        List<String> sources = Collections.emptyList();
        String mode;

        if (request.isUseAgent()) {
            // Agent mode with function calling
            response = agentService.executeAgent(request.getMessage());
            mode = "agent";
        } else if (request.isUseRag()) {
            // RAG mode
            response = ragService.queryWithRag(request.getMessage());
            sources = ragService.getSimilarDocuments(request.getMessage());
            mode = "rag";
        } else {
            // Simple chat mode
            response = chatService.chat(request.getMessage());
            mode = "simple";
        }

        return new ChatResponse(response, sources, mode);
    }

    /**
     * Simple chat endpoint
     */
    @PostMapping("/simple")
    public ChatResponse simpleChat(@RequestParam String message) {
        String response = chatService.chat(message);
        return new ChatResponse(response, Collections.emptyList(), "simple");
    }

    /**
     * RAG-based chat endpoint
     */
    @PostMapping("/rag")
    public ChatResponse ragChat(@RequestParam String question) {
        String response = ragService.queryWithRag(question);
        List<String> sources = ragService.getSimilarDocuments(question);
        return new ChatResponse(response, sources, "rag");
    }

    /**
     * Agent-based chat endpoint
     */
    @PostMapping("/agent")
    public ChatResponse agentChat(@RequestParam String message) {
        String response = agentService.executeAgent(message);
        return new ChatResponse(response, Collections.emptyList(), "agent");
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
