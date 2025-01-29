package com.weather.controller;

import com.weather.model.Coordinates;
import com.weather.model.WeatherResponse;
import com.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * Gets the current temperature for the specified coordinates.
     * Fetches the data from the OpenMeteo API.
     *
     * @param coordinates latitude and longitude as coordinates
     * @return ResponseEntity containing the weather response with temperature data
     */
    @Operation(summary = "Get temperature for coordinates", description = "Gets the current temperature for given latitude and longitude")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved temperature"),
            @ApiResponse(responseCode = "400", description = "Invalid coordinates provided"),
            @ApiResponse(responseCode = "500", description = "Error fetching weather data")
    })
    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(@Valid Coordinates coordinates) {
        return ResponseEntity.ok(weatherService.getWeather(coordinates));
    }

    /**
     * Deletes stored temperature for the specified coordinates.
     *
     * @param coordinates latitude and longitude as coordinates
     * @return ResponseEntity with no content if deletion was successful
     */
    @Operation(summary = "Delete stored temperature", description = "Deletes stored temperature for the specified coordinates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted temperature"),
            @ApiResponse(responseCode = "400", description = "Invalid coordinates")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteWeather(@Valid Coordinates coordinates) {
        weatherService.deleteWeather(coordinates);
        return ResponseEntity.ok().build();
    }
}