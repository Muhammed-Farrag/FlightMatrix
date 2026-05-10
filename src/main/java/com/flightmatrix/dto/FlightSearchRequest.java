package com.flightmatrix.dto;

import com.flightmatrix.enums.SeatClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchRequest {
    private String origin;
    private String destination;
    private LocalDate date;
    private SeatClass seatClass;
    private Integer passengerCount = 1;
}
