package com.tcg.tcgweatherapi.response.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data

public class WeatherResponseDTO {
    private String email;
    private String zipCode;
    private String weatherDetails;
    private LocalDateTime timestamp;

    public WeatherResponseDTO(String email, String zipCode, String weatherDetails, LocalDateTime timestamp) {
        this.email = email;
        this.zipCode = zipCode;
        this.weatherDetails = weatherDetails;
        this.timestamp = timestamp;
    }

    public WeatherResponseDTO() {

    }
}
