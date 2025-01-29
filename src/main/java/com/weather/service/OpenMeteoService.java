package com.weather.service;

import com.weather.model.Coordinates;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class OpenMeteoService {
    
    @Value("${openmeteo.url}")
    private String openMeteoUrl;
    
    private final RestTemplate restTemplate;

    /**
     * Fetches current temperature from the OpenMeteo API for given coordinates.
     *
     * @param coordinates The coordinates to fetch temperature for
     * @return The current temperature in Celsius
     */
    public double fetchTemperature(Coordinates coordinates) {
        String url = UriComponentsBuilder.fromHttpUrl(openMeteoUrl)
                .queryParam("latitude", coordinates.getLatitude())
                .queryParam("longitude", coordinates.getLongitude())
                .queryParam("current_weather", true)
                .build()
                .toUriString();
        
        var response = restTemplate.getForObject(url, OpenMeteoResponse.class);
        return response.current_weather().temperature();
    }
    
    record OpenMeteoResponse(CurrentWeather current_weather) {}
    record CurrentWeather(double temperature) {}
}