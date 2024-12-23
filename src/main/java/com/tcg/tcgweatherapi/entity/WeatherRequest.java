package com.tcg.tcgweatherapi.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
public class WeatherRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String zipCode;
    @Lob // Use Large Object (LOB) for longer text
    @Column(columnDefinition = "TEXT") // For databases supporting TEXT
    private String weatherDetails;
    private LocalDateTime timestamp;
}
