package com.tcg.tcgweatherapi.controller;

import com.tcg.tcgweatherapi.entity.User;
import com.tcg.tcgweatherapi.entity.WeatherRequest;
import com.tcg.tcgweatherapi.request.dto.UserRegistrationRequest;
import com.tcg.tcgweatherapi.response.dto.WeatherResponseDTO;
import com.tcg.tcgweatherapi.service.UserService;
import com.tcg.tcgweatherapi.service.WeatherService;
import com.tcg.tcgweatherapi.validator.ZipCodeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");

        doNothing().when(userService).registerUser(request.getEmail());

        ResponseEntity<String> response = userController.registerUser(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());
        verify(userService, times(1)).registerUser(request.getEmail());
    }

    @Test
    void testRegisterUser_InvalidEmail() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("invalid-email");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userController.registerUser(request);
        });

        assertTrue(exception.getMessage().contains("Email is invalid"));
        verify(userService, never()).registerUser(anyString());
    }

    @Test
    void testGetWeather_UserNotFound() {
        when(userService.getUserByEmail("test@example.com")).thenReturn(null);

        ResponseEntity<WeatherResponseDTO> response = userController.getWeather("test@example.com", "12345");

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("User not found. Please register and then use the API.", response.getBody().getWeatherDetails());
        verify(userService, times(1)).getUserByEmail("test@example.com");
    }

    @Test
    void testGetWeather_UserInactive() {
        User inactiveUser = new User();
        inactiveUser.setActive(false);

        when(userService.getUserByEmail("test@example.com")).thenReturn(inactiveUser);

        ResponseEntity<WeatherResponseDTO> response = userController.getWeather("test@example.com", "12345");

        assertEquals(403, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("User is inactive. Please activate your account to use the API.", response.getBody().getWeatherDetails());
        verify(userService, times(1)).getUserByEmail("test@example.com");
    }

    @Test
    void testGetWeather_InvalidZipCode() {
        User activeUser = new User();
        activeUser.setActive(true);

        when(userService.getUserByEmail("test@example.com")).thenReturn(activeUser);
        mockStatic(ZipCodeValidator.class);
        when(ZipCodeValidator.isValidUSZipCode("invalid-zip")).thenReturn(false);

        ResponseEntity<WeatherResponseDTO> response = userController.getWeather("test@example.com", "invalid-zip");

        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserByEmail("test@example.com");
    }

    @Test
    void testGetWeather_Success() {
        User activeUser = new User();
        activeUser.setActive(true);

        WeatherRequest weatherRequest = new WeatherRequest();
        weatherRequest.setEmail("test@example.com");
        weatherRequest.setZipCode("12345");
        weatherRequest.setWeatherDetails("Sunny");
        weatherRequest.setTimestamp(LocalDateTime.now());

        when(userService.getUserByEmail("test@example.com")).thenReturn(activeUser);
        mockStatic(ZipCodeValidator.class);
        when(ZipCodeValidator.isValidUSZipCode("12345")).thenReturn(true);
        when(weatherService.getWeatherByZipCode("12345")).thenReturn("Sunny");
        when(weatherService.saveWeatherRequest("test@example.com", "12345", "Sunny")).thenReturn(weatherRequest);

        ResponseEntity<WeatherResponseDTO> response = userController.getWeather("test@example.com", "12345");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Sunny", response.getBody().getWeatherDetails());
        assertEquals("test@example.com", response.getBody().getEmail());
        assertEquals("12345", response.getBody().getZipCode());
        verify(userService, times(1)).getUserByEmail("test@example.com");
    }

    @Test
    void testGetHistory_Success() {
        WeatherRequest request1 = new WeatherRequest();
        request1.setEmail("test@example.com");
        request1.setZipCode("12345");
        request1.setWeatherDetails("Sunny");
        request1.setTimestamp(LocalDateTime.now());

        WeatherRequest request2 = new WeatherRequest();
        request2.setEmail("test@example.com");
        request2.setZipCode("67890");
        request2.setWeatherDetails("Rainy");
        request2.setTimestamp(LocalDateTime.now());

        when(weatherService.getHistory("12345", "test@example.com")).thenReturn(List.of(request1, request2));

        ResponseEntity<List<WeatherResponseDTO>> response = userController.getHistory("12345", "test@example.com");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(weatherService, times(1)).getHistory("12345", "test@example.com");
    }

    @Test
    void testActivateUser() {
        doNothing().when(userService).activateUser("test@example.com");

        ResponseEntity<String> response = userController.activateUser("test@example.com");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User activated successfully", response.getBody());
        verify(userService, times(1)).activateUser("test@example.com");
    }

    @Test
    void testDeactivateUser() {
        doNothing().when(userService).deactivateUser("test@example.com");

        ResponseEntity<String> response = userController.deactivateUser("test@example.com");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User deactivated successfully", response.getBody());
        verify(userService, times(1)).deactivateUser("test@example.com");
    }
}
