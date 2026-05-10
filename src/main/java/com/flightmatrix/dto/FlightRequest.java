package com.flightmatrix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightRequest {
    private String flightNumber;
    private String airline;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer totalSeats;
    private Double economyPrice;
    private Double businessPrice;
    private Double firstClassPrice;
    private String flightType; // "DOMESTIC" or "INTERNATIONAL"

    // Domestic only
    private Boolean roundTrip;

    // International only
    private Boolean visaRequired;
    private String transitCountries;
}
