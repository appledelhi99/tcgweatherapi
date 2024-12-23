package com.tcg.tcgweatherapi.service;

import com.tcg.tcgweatherapi.entity.WeatherRequest;
import com.tcg.tcgweatherapi.repository.WeatherRequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class responsible for managing weather-related operations.
 *
 * <p>This class provides methods to fetch weather data from an external API,
 * save weather request details, and retrieve the history of weather requests
 * based on user email or zip code.</p>
 */
@Service
public class WeatherService {

    @Value("${weather.api.url}")
    private String weatherApiUrl;

    @Value("${weather.api.appid}")
    private String appId;

    private final RestTemplate restTemplate;
    private final WeatherRequestRepository weatherRequestRepository;

    /**
     * Constructor for dependency injection.
     *
     * @param restTemplate             the {@link RestTemplate} instance for making HTTP requests.
     * @param weatherRequestRepository the repository for managing {@link WeatherRequest} entities.
     */
    public WeatherService(RestTemplate restTemplate, WeatherRequestRepository weatherRequestRepository) {
        this.restTemplate = restTemplate;
        this.weatherRequestRepository = weatherRequestRepository;
    }

    /**
     * Fetches weather information for a specific zip code using an external weather API.
     *
     * <p>The method constructs the API URL using the provided zip code, app ID, and API base URL.
     * If the request is successful, the weather data is returned as a JSON string. In case of errors,
     * appropriate runtime exceptions are thrown.</p>
     *
     * @param zipCode the zip code for which weather data is to be retrieved.
     * @return a JSON string containing weather details.
     * @throws RuntimeException if an HTTP or general error occurs while fetching weather data.
     */
    public String getWeatherByZipCode(String zipCode) {
        String url = String.format("%s?zip=%s&appid=%s&units=imperial", weatherApiUrl, zipCode, appId);
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException e) {
            // Handle specific HTTP errors (e.g., 404 or 401)
            throw new RuntimeException("Error fetching weather data: " + e.getMessage());
        } catch (Exception e) {
            // Handle generic errors
            throw new RuntimeException("Unexpected error occurred while fetching weather data: " + e.getMessage());
        }
    }

    /**
     * Saves weather request details to the database.
     *
     * <p>The method creates a new {@link WeatherRequest} entity with the provided email,
     * zip code, and weather details, along with the current timestamp. The entity is then
     * persisted to the database using the {@link WeatherRequestRepository}.</p>
     *
     * @param email          the email address of the user making the request.
     * @param zipCode        the zip code for which weather data was requested.
     * @param weatherDetails the weather details retrieved from the external API.
     * @return the saved {@link WeatherRequest} entity.
     */
    public WeatherRequest saveWeatherRequest(String email, String zipCode, String weatherDetails) {
        WeatherRequest weatherRequest = new WeatherRequest();
        weatherRequest.setEmail(email);
        weatherRequest.setZipCode(zipCode);
        weatherRequest.setWeatherDetails(weatherDetails);
        weatherRequest.setTimestamp(LocalDateTime.now());
        return weatherRequestRepository.save(weatherRequest);
    }

    /**
     * Retrieves the history of weather requests based on email or zip code.
     *
     * <p>This method queries the database for weather requests that match the specified
     * email or zip code. If both parameters are null, it retrieves all records.</p>
     *
     * @param zipCode the zip code to filter the history (optional).
     * @param email   the user's email address to filter the history (optional).
     * @return a list of {@link WeatherRequest} entities matching the criteria.
     */
    public List<WeatherRequest> getHistory(String zipCode, String email) {
        return weatherRequestRepository.findByZipCodeOrEmail(zipCode, email);
    }
}
