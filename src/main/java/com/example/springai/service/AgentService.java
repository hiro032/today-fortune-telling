package com.example.springai.service;

import com.example.springai.agent.CalculatorFunction;
import com.example.springai.agent.FortuneFunction;
import com.example.springai.agent.WeatherFunction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class AgentService {

    private final ChatClient chatClient;

    public AgentService(ChatModel chatModel,
                       WeatherFunction weatherFunction,
                       CalculatorFunction calculatorFunction,
                       FortuneFunction fortuneFunction) {
        this.chatClient = ChatClient.builder(chatModel)
            .defaultFunctions("weatherFunction", "calculatorFunction", "fortuneFunction")
            .build();
    }

    /**
     * Execute agent with function calling capabilities
     */
    public String executeAgent(String userMessage) {
        return chatClient.prompt()
            .user(userMessage)
            .call()
            .content();
    }

    /**
     * Execute agent with custom system message
     */
    public String executeAgentWithSystemMessage(String systemMessage, String userMessage) {
        return chatClient.prompt()
            .system(systemMessage)
            .user(userMessage)
            .call()
            .content();
    }
}
