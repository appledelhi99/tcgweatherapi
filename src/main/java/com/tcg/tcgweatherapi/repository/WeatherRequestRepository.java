package com.tcg.tcgweatherapi.repository;

import com.tcg.tcgweatherapi.entity.WeatherRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherRequestRepository extends JpaRepository<WeatherRequest, Long> {
    List<WeatherRequest> findByZipCodeOrEmail(String zipCode, String email);
}