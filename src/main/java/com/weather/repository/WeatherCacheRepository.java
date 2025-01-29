package com.weather.repository;

import com.weather.model.WeatherCache;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.time.LocalDateTime;
import java.util.Optional;

public interface WeatherCacheRepository extends MongoRepository<WeatherCache, String> {

    /**
     * Finds valid cached temperature for the specified coordinates and minimum timestamp.
     *
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @param minTimestamp The minimum timestamp for valid cache
     * @return Optional containing the weather cache if found
     */
    @Query("{'latitude': ?0, 'longitude': ?1, 'timestamp': {$gte: ?2}}")
    Optional<WeatherCache> findValidCache(double latitude, double longitude, LocalDateTime minTimestamp);

    /**
     * Deletes temperature for given coordinates.
     *
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     */
    void deleteByLatitudeAndLongitude(double latitude, double longitude);
}