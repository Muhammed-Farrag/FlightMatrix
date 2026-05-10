package com.flightmatrix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerRequest {
    private String name;
    private String passportNumber;
    private LocalDate dateOfBirth;
    private String specialRequests;
}
