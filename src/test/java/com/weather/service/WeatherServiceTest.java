package com.weather.service;

import com.weather.model.Coordinates;
import com.weather.model.WeatherCache;
import com.weather.model.WeatherResponse;
import com.weather.repository.WeatherCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class WeatherServiceTest {

    @MockBean
    private WeatherCacheRepository weatherCacheRepository;

    @MockBean
    private OpenMeteoService openMeteoService;

    @Autowired
    private WeatherService weatherService;

    private Coordinates testCoordinates;
    private WeatherCache testCachedCoordinates;

    @BeforeEach
    void setUp() {
        testCoordinates = new Coordinates();
        testCoordinates.setLatitude(36.7178);
        testCoordinates.setLongitude(4.4256);

        testCachedCoordinates = new WeatherCache();
        testCachedCoordinates.setLatitude(36.7178);
        testCachedCoordinates.setLongitude(4.4256);
        testCachedCoordinates.setTemperature(20.5);
        testCachedCoordinates.setTimestamp(LocalDateTime.now());
    }

    @Test
    void getTemperatureFromCache() {
        when(weatherCacheRepository.findValidCache(
                anyDouble(), anyDouble(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(testCachedCoordinates));

        WeatherResponse response = weatherService.getWeather(testCoordinates);

        assertNotNull(response);
        assertEquals(testCachedCoordinates.getLatitude(), response.getLatitude());
        assertEquals(testCachedCoordinates.getLongitude(), response.getLongitude());
        assertEquals(testCachedCoordinates.getTemperature(), response.getTemperature());

        verify(openMeteoService, never()).fetchTemperature(any());
        verify(weatherCacheRepository, never()).save(any());
    }

    @Test
    void getWeather_no_cache() {
        double expectedTemperature = 25.0;
        when(weatherCacheRepository.findValidCache(
                anyDouble(), anyDouble(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(openMeteoService.fetchTemperature(any())).thenReturn(expectedTemperature);
        when(weatherCacheRepository.save(any())).thenReturn(testCachedCoordinates);

        WeatherResponse response = weatherService.getWeather(testCoordinates);

        assertNotNull(response);
        assertEquals(testCoordinates.getLatitude(), response.getLatitude());
        assertEquals(testCoordinates.getLongitude(), response.getLongitude());
        assertEquals(expectedTemperature, response.getTemperature());

        verify(openMeteoService).fetchTemperature(testCoordinates);
        verify(weatherCacheRepository).save(any(WeatherCache.class));
    }

    @Test
    void getWeather_expired_cache() {
        testCachedCoordinates.setTimestamp(LocalDateTime.now().minusMinutes(2));
        double expectedTemperature = 25.0;

        when(weatherCacheRepository.findValidCache(
                anyDouble(), anyDouble(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(openMeteoService.fetchTemperature(any())).thenReturn(expectedTemperature);

        WeatherResponse response = weatherService.getWeather(testCoordinates);

        assertNotNull(response);
        assertEquals(expectedTemperature, response.getTemperature());

        verify(openMeteoService).fetchTemperature(testCoordinates);
        verify(weatherCacheRepository).save(any(WeatherCache.class));
    }

    @Test
    void deleteWeather() {
        weatherService.deleteWeather(testCoordinates);

        verify(weatherCacheRepository).deleteByLatitudeAndLongitude(
                testCoordinates.getLatitude(),
                testCoordinates.getLongitude()
        );
    }
}
