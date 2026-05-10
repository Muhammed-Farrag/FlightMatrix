package com.flightmatrix.service;

import com.flightmatrix.dto.FlightRequest;
import com.flightmatrix.dto.FlightSearchRequest;
import com.flightmatrix.entity.DomesticFlight;
import com.flightmatrix.entity.Flight;
import com.flightmatrix.entity.InternationalFlight;
import com.flightmatrix.enums.SeatClass;
import com.flightmatrix.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Transactional(readOnly = true)
    public List<Flight> searchFlights(FlightSearchRequest req) {
        List<Flight> flights;
        if (req.getDate() != null) {
            LocalDateTime start = req.getDate().atStartOfDay();
            LocalDateTime end   = req.getDate().atTime(23, 59, 59);
            flights = flightRepository.findByOriginAndDestinationAndDepartureTimeBetween(
                    req.getOrigin(), req.getDestination(), start, end);
        } else {
            flights = flightRepository.findByOriginAndDestination(
                    req.getOrigin(), req.getDestination());
        }
        int count = req.getPassengerCount() != null ? req.getPassengerCount() : 1;
        return flights.stream()
                .filter(f -> f.checkAvailability(count))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Flight getFlightById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found: " + id));
    }

    public Flight addFlight(FlightRequest req) {
        if (flightRepository.existsByFlightNumber(req.getFlightNumber())) {
            throw new RuntimeException("Flight number already exists: " + req.getFlightNumber());
        }
        return flightRepository.save(buildFlight(req));
    }

    public Flight updateFlight(String flightNumber, FlightRequest req) {
        Flight flight = flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new RuntimeException("Flight not found: " + flightNumber));
        flight.setAirline(req.getAirline());
        flight.setOrigin(req.getOrigin());
        flight.setDestination(req.getDestination());
        flight.setDepartureTime(req.getDepartureTime());
        flight.setArrivalTime(req.getArrivalTime());
        flight.setEconomyPrice(req.getEconomyPrice());
        flight.setBusinessPrice(req.getBusinessPrice());
        flight.setFirstClassPrice(req.getFirstClassPrice());
        if (req.getTotalSeats() != null) {
            flight.setTotalSeats(req.getTotalSeats());
        }
        return flightRepository.save(flight);
    }

    public double calculatePrice(Long flightId, SeatClass seatClass) {
        return getFlightById(flightId).calculatePrice(seatClass);
    }

    private Flight buildFlight(FlightRequest req) {
        if ("DOMESTIC".equalsIgnoreCase(req.getFlightType())) {
            return new DomesticFlight(
                req.getFlightNumber(), req.getAirline(), req.getOrigin(), req.getDestination(),
                req.getDepartureTime(), req.getArrivalTime(), req.getTotalSeats(),
                req.getEconomyPrice(), req.getBusinessPrice(), req.getFirstClassPrice(),
                Boolean.TRUE.equals(req.getRoundTrip())
            );
        }
        return new InternationalFlight(
            req.getFlightNumber(), req.getAirline(), req.getOrigin(), req.getDestination(),
            req.getDepartureTime(), req.getArrivalTime(), req.getTotalSeats(),
            req.getEconomyPrice(), req.getBusinessPrice(), req.getFirstClassPrice(),
            Boolean.TRUE.equals(req.getVisaRequired()),
            req.getTransitCountries() != null ? req.getTransitCountries() : ""
        );
    }
}
