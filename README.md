# Weather API

A Spring Boot application that provides temperature for given coordinates, using the Open-Meteo API as the data source.
Caches the temperatures using MongoDB.

# Start the application

Run `docker-compose up --build`

The application will be available at http://localhost:8080

# Endpoints

Get Weather Data

`GET /weather?latitude={lat}&longitude={lon}`

Delete Weather Data

`DELETE /weather?latitude={lat}&longitude={lon}`

# Documentation

Access the Swagger UI at http://localhost:8080/swagger-ui.html
