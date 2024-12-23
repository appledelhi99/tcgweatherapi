package com.tcg.tcgweatherapi.controller;

import com.tcg.tcgweatherapi.entity.User;
import com.tcg.tcgweatherapi.entity.WeatherRequest;
import com.tcg.tcgweatherapi.exceptions.InvalidEmailFormatException;
import com.tcg.tcgweatherapi.request.dto.UserRegistrationRequest;
import com.tcg.tcgweatherapi.response.dto.WeatherResponseDTO;
import com.tcg.tcgweatherapi.service.UserService;
import com.tcg.tcgweatherapi.service.WeatherService;
import com.tcg.tcgweatherapi.validator.ZipCodeValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users") // API versioning added
@Tag(name = "User API", description = "APIs for user management and weather-related operations")
public class UserController {

    private final UserService userService;
    private final WeatherService weatherService;

    public UserController(UserService userService, WeatherService weatherService) {
        this.userService = userService;
        this.weatherService = weatherService;
    }

    /**
     * Registers a new user with the provided email address.
     *
     * @param request the user registration request containing an email
     * @return a response indicating success or failure
     */
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user by accepting an email. The email must be in a valid format.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid email format", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request) {
        if (!request.getEmail().contains("@")) {
            throw new InvalidEmailFormatException("Email is invalid");
        }
        userService.registerUser(request.getEmail());
        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * Retrieves weather information for a registered user based on their email and zip code.
     *
     * @param email   the user's email
     * @param zipCode the zip code for weather information
     * @return the weather details and timestamp
     */
    @Operation(
            summary = "Get weather information",
            description = "Retrieves weather details for a registered user based on their email and zip code.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Weather details retrieved successfully"),
                    @ApiResponse(responseCode = "403", description = "User not active or not registered"),
                    @ApiResponse(responseCode = "500", description = "Error fetching weather details")
            }
    )
    @GetMapping("/weather")
    public ResponseEntity<WeatherResponseDTO> getWeather(
            @Parameter(description = "The user's email address") @RequestParam String email,
            @Parameter(description = "The zip code for weather information") @RequestParam String zipCode) {

        User user = userService.getUserByEmail(email);
        if (user == null) {
            // User not found
            WeatherResponseDTO errorResponse = new WeatherResponseDTO();
            errorResponse.setWeatherDetails("User not found. Please register and then use the API.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (!user.isActive()) {
            // User is inactive
            WeatherResponseDTO errorResponse = new WeatherResponseDTO();
            errorResponse.setWeatherDetails("User is inactive. Please activate your account to use the API.");
            return ResponseEntity.status(403).body(errorResponse);
        }

        // Validate the ZIP code
        if (!ZipCodeValidator.isValidUSZipCode(zipCode)) {
            return ResponseEntity.badRequest().body(null); // Or throw a custom exception
        }

        String weatherDetails = weatherService.getWeatherByZipCode(zipCode);
        WeatherRequest weatherRequest = weatherService.saveWeatherRequest(email, zipCode, weatherDetails);

        WeatherResponseDTO response = new WeatherResponseDTO();
        response.setEmail(email);
        response.setZipCode(zipCode);
        response.setWeatherDetails(weatherDetails);
        response.setTimestamp(weatherRequest.getTimestamp());

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the history of weather requests based on optional email and/or zip code.
     *
     * @param zipCode the zip code to filter the history (optional)
     * @param email   the user's email to filter the history (optional)
     * @return a list of weather request history
     */
    @Operation(
            summary = "Get weather request history",
            description = "Retrieves the history of weather requests for a specific user or zip code.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Weather request history retrieved successfully")
            }
    )
    @GetMapping("/history")
    public ResponseEntity<List<WeatherResponseDTO>> getHistory(
            @Parameter(description = "The zip code to filter the history") @RequestParam(required = false) String zipCode,
            @Parameter(description = "The user's email to filter the history") @RequestParam(required = false) String email) {

        List<WeatherRequest> history = weatherService.getHistory(zipCode, email);
        List<WeatherResponseDTO> response = history.stream()
                .map(request -> new WeatherResponseDTO(
                        request.getEmail(),
                        request.getZipCode(),
                        request.getWeatherDetails(),
                        request.getTimestamp()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Activates a user's account.
     *
     * @param email the user's email
     * @return a response indicating success
     */
    @Operation(
            summary = "Activate a user",
            description = "Activates a user account based on the provided email.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User activated successfully")
            }
    )
    @PostMapping("/activate")
    public ResponseEntity<String> activateUser(
            @Parameter(description = "The email address of the user to activate") @RequestParam String email) {

        userService.activateUser(email);
        return ResponseEntity.ok("User activated successfully");
    }

    /**
     * Deactivates a user's account.
     *
     * @param email the user's email
     * @return a response indicating success
     */
    @Operation(
            summary = "Deactivate a user",
            description = "Deactivates a user account based on the provided email.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deactivated successfully")
            }
    )
    @PostMapping("/deactivate")
    public ResponseEntity<String> deactivateUser(
            @Parameter(description = "The email address of the user to deactivate") @RequestParam String email) {

        userService.deactivateUser(email);
        return ResponseEntity.ok("User deactivated successfully");
    }
}
