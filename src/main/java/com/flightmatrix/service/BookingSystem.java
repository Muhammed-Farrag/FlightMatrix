package com.flightmatrix.service;

import com.flightmatrix.dto.*;
import com.flightmatrix.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Central coordinator — satisfies the OOP requirement for BookingSystem.
 * Controllers depend on this class, not on individual services directly.
 */
@Service
public class BookingSystem {

    private final UserService userService;
    private final FlightService flightService;
    private final BookingService bookingService;
    private final PaymentService paymentService;

    public BookingSystem(UserService userService,
                         FlightService flightService,
                         BookingService bookingService,
                         PaymentService paymentService) {
        this.userService    = userService;
        this.flightService  = flightService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
    }

    public List<Flight> searchFlights(FlightSearchRequest request) {
        return flightService.searchFlights(request);
    }

    public Booking createBooking(BookingRequest request, Long customerId) {
        return bookingService.createBooking(request, customerId);
    }

    public Booking cancelBooking(String bookingReference) {
        return bookingService.cancelBooking(bookingReference);
    }

    public Booking confirmBooking(String bookingReference) {
        return bookingService.confirmBooking(bookingReference);
    }

    public Payment processPayment(PaymentRequest request) {
        return paymentService.processPayment(request);
    }

    public String generateTicket(String bookingReference) {
        return bookingService.generateItinerary(bookingReference);
    }

    public User registerUser(RegisterRequest request) {
        return userService.register(request);
    }
}
