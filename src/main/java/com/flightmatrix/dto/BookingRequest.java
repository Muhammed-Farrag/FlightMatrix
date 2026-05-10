package com.flightmatrix.dto;

import com.flightmatrix.enums.SeatClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private Long flightId;
    private SeatClass seatClass;
    private List<PassengerRequest> passengers;
    private String specialRequests;
    private Long customerId; // optional: agents supply this to book on behalf of a customer
}
