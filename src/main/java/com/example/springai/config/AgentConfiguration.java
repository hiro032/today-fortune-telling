package com.example.springai.config;

import com.example.springai.agent.CalculatorFunction;
import com.example.springai.agent.FortuneFunction;
import com.example.springai.agent.WeatherFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
public class AgentConfiguration {

    @Bean
    @Description("Get the current weather for a given location")
    public WeatherFunction weatherFunction() {
        return new WeatherFunction();
    }

    @Bean
    @Description("Perform mathematical calculations: add, subtract, multiply, divide")
    public CalculatorFunction calculatorFunction() {
        return new CalculatorFunction();
    }

    @Bean
    @Description("Tell fortune based on name, gender, and birth date")
    public FortuneFunction fortuneFunction() {
        return new FortuneFunction();
    }
}
