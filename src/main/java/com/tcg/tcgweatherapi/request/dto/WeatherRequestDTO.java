package com.tcg.tcgweatherapi.request.dto;

import lombok.Data;

@Data
public class WeatherRequestDTO {
    private String email;
    private String zipCode;

}
