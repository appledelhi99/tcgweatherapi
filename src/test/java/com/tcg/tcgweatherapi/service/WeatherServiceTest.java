package com.tcg.tcgweatherapi.service;

import com.tcg.tcgweatherapi.entity.WeatherRequest;
import com.tcg.tcgweatherapi.repository.WeatherRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WeatherRequestRepository weatherRequestRepository;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetWeatherByZipCode_Success() {
        String zipCode = "10001";
        String weatherApiResponse = "{ \"weather\": \"Sunny\" }";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(weatherApiResponse);

        String result = weatherService.getWeatherByZipCode(zipCode);

        assertNotNull(result);
        assertEquals(weatherApiResponse, result);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetWeatherByZipCode_HttpClientErrorException() {
        String zipCode = "10001";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenThrow(HttpClientErrorException.class);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            weatherService.getWeatherByZipCode(zipCode);
        });

        assertTrue(exception.getMessage().contains("Error fetching weather data"));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testGetWeatherByZipCode_GenericException() {
        String zipCode = "10001";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenThrow(RuntimeException.class);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            weatherService.getWeatherByZipCode(zipCode);
        });

        assertTrue(exception.getMessage().contains("Unexpected error occurred while fetching weather data"));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testSaveWeatherRequest_Success() {
        String email = "test@example.com";
        String zipCode = "10001";
        String weatherDetails = "Sunny";

        WeatherRequest weatherRequest = new WeatherRequest();
        weatherRequest.setEmail(email);
        weatherRequest.setZipCode(zipCode);
        weatherRequest.setWeatherDetails(weatherDetails);
        weatherRequest.setTimestamp(LocalDateTime.now());

        when(weatherRequestRepository.save(any(WeatherRequest.class))).thenReturn(weatherRequest);

        WeatherRequest result = weatherService.saveWeatherRequest(email, zipCode, weatherDetails);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(zipCode, result.getZipCode());
        assertEquals(weatherDetails, result.getWeatherDetails());
        verify(weatherRequestRepository, times(1)).save(any(WeatherRequest.class));
    }

    @Test
    void testGetHistory_Success() {
        String email = "test@example.com";
        String zipCode = "10001";

        WeatherRequest request1 = new WeatherRequest();
        request1.setEmail(email);
        request1.setZipCode(zipCode);
        request1.setWeatherDetails("Sunny");
        request1.setTimestamp(LocalDateTime.now());

        WeatherRequest request2 = new WeatherRequest();
        request2.setEmail(email);
        request2.setZipCode("10001");
        request2.setWeatherDetails("Rainy");
        request2.setTimestamp(LocalDateTime.now());

        List<WeatherRequest> history = Arrays.asList(request1, request2);

        when(weatherRequestRepository.findByZipCodeOrEmail(zipCode, email)).thenReturn(history);

        List<WeatherRequest> result = weatherService.getHistory(zipCode, email);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Sunny", result.get(0).getWeatherDetails());
        assertEquals("Rainy", result.get(1).getWeatherDetails());
        verify(weatherRequestRepository, times(1)).findByZipCodeOrEmail(zipCode, email);
    }

    @Test
    void testGetHistory_NoResults() {
        String email = "test@example.com";
        String zipCode = "10001";

        when(weatherRequestRepository.findByZipCodeOrEmail(zipCode, email)).thenReturn(List.of());

        List<WeatherRequest> result = weatherService.getHistory(zipCode, email);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(weatherRequestRepository, times(1)).findByZipCodeOrEmail(zipCode, email);
    }
}
