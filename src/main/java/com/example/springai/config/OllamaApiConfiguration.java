package com.example.springai.config;

import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for OllamaApi with ngrok support
 */
@Configuration
public class OllamaApiConfiguration {

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    /**
     * Create OllamaApi with custom headers for ngrok free tier
     * Adds "ngrok-skip-browser-warning" header to bypass ngrok's browser check
     */
    @Bean
    @ConditionalOnMissingBean
    public OllamaApi ollamaApi() {
        // Create RestClient with ngrok bypass header
        RestClient.Builder restClientBuilder = RestClient.builder()
            .baseUrl(ollamaBaseUrl)
            .requestInterceptor((request, body, execution) -> {
                // Add ngrok bypass header for production environment
                request.getHeaders().add("ngrok-skip-browser-warning", "true");
                request.getHeaders().add("User-Agent", "Spring-AI-Ollama-Client");
                return execution.execute(request, body);
            });

        // WebClient for streaming support
        WebClient.Builder webClientBuilder = WebClient.builder()
            .baseUrl(ollamaBaseUrl)
            .defaultHeader("ngrok-skip-browser-warning", "true")
            .defaultHeader("User-Agent", "Spring-AI-Ollama-Client");

        return new OllamaApi(ollamaBaseUrl, restClientBuilder, webClientBuilder);
    }
}
