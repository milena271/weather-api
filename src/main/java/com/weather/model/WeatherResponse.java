package com.weather.model;

import lombok.Data;

@Data
public class WeatherResponse {
    private double latitude;
    private double longitude;
    private double temperature;
}