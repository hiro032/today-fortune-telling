package com.example.springai.agent;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.function.Function;

/**
 * Example function for weather lookup
 */
public class WeatherFunction implements Function<WeatherFunction.Request, WeatherFunction.Response> {

    public record Request(
        @JsonProperty(required = true, value = "location")
        @JsonPropertyDescription("The city and state, e.g. San Francisco, CA")
        String location,

        @JsonProperty(required = false, value = "unit")
        @JsonPropertyDescription("Temperature unit: celsius or fahrenheit")
        String unit
    ) {}

    public record Response(
        String location,
        double temperature,
        String unit,
        String condition
    ) {}

    @Override
    public Response apply(Request request) {
        // This is a mock implementation
        // In a real scenario, you would call an actual weather API
        double temp = Math.random() * 30 + 10; // Random temp between 10-40
        String unit = request.unit() != null ? request.unit() : "celsius";
        String[] conditions = {"Sunny", "Cloudy", "Rainy", "Partly Cloudy"};
        String condition = conditions[(int) (Math.random() * conditions.length)];

        return new Response(
            request.location(),
            Math.round(temp * 10.0) / 10.0,
            unit,
            condition
        );
    }
}
