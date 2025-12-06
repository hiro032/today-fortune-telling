package com.example.springai.agent;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.function.Function;

/**
 * Example calculator function for mathematical operations
 */
public class CalculatorFunction implements Function<CalculatorFunction.Request, CalculatorFunction.Response> {

    public record Request(
        @JsonProperty(required = true, value = "operation")
        @JsonPropertyDescription("The mathematical operation: add, subtract, multiply, divide")
        String operation,

        @JsonProperty(required = true, value = "a")
        @JsonPropertyDescription("First number")
        double a,

        @JsonProperty(required = true, value = "b")
        @JsonPropertyDescription("Second number")
        double b
    ) {}

    public record Response(
        String operation,
        double a,
        double b,
        double result
    ) {}

    @Override
    public Response apply(Request request) {
        double result = switch (request.operation().toLowerCase()) {
            case "add" -> request.a() + request.b();
            case "subtract" -> request.a() - request.b();
            case "multiply" -> request.a() * request.b();
            case "divide" -> {
                if (request.b() == 0) {
                    throw new IllegalArgumentException("Cannot divide by zero");
                }
                yield request.a() / request.b();
            }
            default -> throw new IllegalArgumentException("Unknown operation: " + request.operation());
        };

        return new Response(request.operation(), request.a(), request.b(), result);
    }
}
