package com.weather.service;

import com.weather.model.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class OpenMeteoServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private OpenMeteoService openMeteoService;

    private Coordinates testCoordinates;

    @BeforeEach
    void setUp() {
        testCoordinates = new Coordinates();
        testCoordinates.setLatitude(36.7178);
        testCoordinates.setLongitude(4.4256);
    }

    @Test
    void fetchTemperature() {
        when(restTemplate.getForObject(anyString(), any()))
                .thenReturn(new OpenMeteoService.OpenMeteoResponse(
                        new OpenMeteoService.CurrentWeather(42)));
        double temperature = openMeteoService.fetchTemperature(testCoordinates);
        assertEquals(42, temperature);
    }

    @Test
    void fetchTemperature_api_error() {
        when(restTemplate.getForObject(anyString(), any()))
                .thenThrow(new RuntimeException("API Error"));
        assertThrows(RuntimeException.class, () ->
                openMeteoService.fetchTemperature(testCoordinates)
        );
    }
}