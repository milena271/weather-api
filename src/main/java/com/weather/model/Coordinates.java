package com.weather.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class Coordinates {
    @Min(-90) @Max(90)
    private double latitude;
    
    @Min(-180) @Max(180)
    private double longitude;
}