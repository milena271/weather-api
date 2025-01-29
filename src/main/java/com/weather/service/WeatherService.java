package com.weather.service;

import com.weather.model.Coordinates;
import com.weather.model.WeatherCache;
import com.weather.model.WeatherResponse;
import com.weather.repository.WeatherCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WeatherService {
    
    private final WeatherCacheRepository weatherCacheRepository;
    private final OpenMeteoService openMeteoService;
    
    @Value("${cache.expiry-minutes}")
    private int cacheExpiryMinutes;

    /**
     * Retrieves temperature for the specified coordinates, either from cache or from OpenMeteo API.
     *
     * @param coordinates The coordinates to get the temperature
     * @return WeatherResponse containing the temperature
     */
    public WeatherResponse getWeather(Coordinates coordinates) {
        var minTimestamp = LocalDateTime.now().minusMinutes(cacheExpiryMinutes);
        
        return weatherCacheRepository.findValidCache(
                coordinates.getLatitude(),
                coordinates.getLongitude(),
                minTimestamp
        ).map(this::toResponse)
         .orElseGet(() -> fetchAndCacheWeather(coordinates));
    }

    /**
     * Deletes cached temperature for the specified coordinates.
     *
     * @param coordinates The coordinates to delete the cache
     */
    public void deleteWeather(Coordinates coordinates) {
        weatherCacheRepository.deleteByLatitudeAndLongitude(
                coordinates.getLatitude(),
                coordinates.getLongitude()
        );
    }
    
    private WeatherResponse fetchAndCacheWeather(Coordinates coordinates) {
        double temperature = openMeteoService.fetchTemperature(coordinates);
        
        var weatherCache = new WeatherCache();
        weatherCache.setLatitude(coordinates.getLatitude());
        weatherCache.setLongitude(coordinates.getLongitude());
        weatherCache.setTemperature(temperature);
        weatherCache.setTimestamp(LocalDateTime.now());
        
        weatherCacheRepository.save(weatherCache);
        
        return toResponse(weatherCache);
    }
    
    private WeatherResponse toResponse(WeatherCache weatherCache) {
        var response = new WeatherResponse();
        response.setLatitude(weatherCache.getLatitude());
        response.setLongitude(weatherCache.getLongitude());
        response.setTemperature(weatherCache.getTemperature());
        return response;
    }
}