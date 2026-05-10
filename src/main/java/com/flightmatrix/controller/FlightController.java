package com.flightmatrix.controller;

import com.flightmatrix.dto.ApiResponse;
import com.flightmatrix.dto.FlightRequest;
import com.flightmatrix.dto.FlightSearchRequest;
import com.flightmatrix.entity.Flight;
import com.flightmatrix.enums.SeatClass;
import com.flightmatrix.service.BookingSystem;
import com.flightmatrix.service.FlightService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final BookingSystem bookingSystem;
    private final FlightService flightService;

    public FlightController(BookingSystem bookingSystem, FlightService flightService) {
        this.bookingSystem = bookingSystem;
        this.flightService = flightService;
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> search(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) SeatClass seatClass,
            @RequestParam(defaultValue = "1") Integer passengerCount) {
        FlightSearchRequest req = new FlightSearchRequest(origin, destination, date, seatClass, passengerCount);
        List<Flight> flights = bookingSystem.searchFlights(req);
        return ResponseEntity.ok(ApiResponse.success(flights));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> all() {
        return ResponseEntity.ok(ApiResponse.success(flightService.getAllFlights()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(flightService.getFlightById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> addFlight(@RequestBody FlightRequest req) {
        try {
            Flight flight = flightService.addFlight(req);
            return ResponseEntity.status(201).body(ApiResponse.success("Flight added", flight));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{flightNumber}")
    public ResponseEntity<ApiResponse<?>> updateFlight(@PathVariable String flightNumber,
                                                       @RequestBody FlightRequest req) {
        try {
            Flight flight = flightService.updateFlight(flightNumber, req);
            return ResponseEntity.ok(ApiResponse.success("Flight updated", flight));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/price")
    public ResponseEntity<ApiResponse<?>> getPrice(@PathVariable Long id,
                                                   @RequestParam SeatClass seatClass) {
        try {
            double price = flightService.calculatePrice(id, seatClass);
            return ResponseEntity.ok(ApiResponse.success(price));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }
}
