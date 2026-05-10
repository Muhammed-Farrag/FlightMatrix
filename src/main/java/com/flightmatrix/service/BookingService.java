package com.flightmatrix.service;

import com.flightmatrix.dto.BookingRequest;
import com.flightmatrix.dto.PassengerRequest;
import com.flightmatrix.entity.*;
import com.flightmatrix.enums.BookingStatus;
import com.flightmatrix.repository.BookingRepository;
import com.flightmatrix.repository.FlightRepository;
import com.flightmatrix.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          FlightRepository flightRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository  = flightRepository;
        this.userRepository    = userRepository;
    }

    public Booking createBooking(BookingRequest req, Long customerId) {
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("User not found: " + customerId));
        if (!(user instanceof Customer customer)) {
            throw new RuntimeException("User is not a customer");
        }

        Flight flight = flightRepository.findById(req.getFlightId())
                .orElseThrow(() -> new RuntimeException("Flight not found: " + req.getFlightId()));

        int passengerCount = req.getPassengers().size();
        if (!flight.checkAvailability(passengerCount)) {
            throw new RuntimeException("Not enough seats available on " + flight.getFlightNumber());
        }

        String pnr = generateUniquePNR();
        double pricePerPassenger = flight.calculatePrice(req.getSeatClass());
        double totalPrice = pricePerPassenger * passengerCount;

        Booking booking = new Booking(pnr, customer, flight, req.getSeatClass(), totalPrice);

        for (PassengerRequest pr : req.getPassengers()) {
            Passenger passenger = new Passenger(
                pr.getName(), pr.getPassportNumber(),
                pr.getDateOfBirth(), pr.getSpecialRequests()
            );
            booking.addPassenger(passenger);
            flight.reserveSeat();
        }

        flightRepository.save(flight);
        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public Booking getBookingByReference(String reference) {
        return bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + reference));
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsForCustomer(Long customerId) {
        return bookingRepository.findByCustomer_UserId(customerId);
    }

    public Booking confirmBooking(String reference) {
        Booking booking = getBookingByReference(reference);
        booking.confirmBooking();
        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(String reference) {
        Booking booking = getBookingByReference(reference);
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }
        booking.cancel();
        flightRepository.save(booking.getFlight());
        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public String generateItinerary(String reference) {
        return getBookingByReference(reference).generateItinerary();
    }

    private String generateUniquePNR() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        String pnr;
        do {
            StringBuilder sb = new StringBuilder("FM-");
            for (int i = 0; i < 6; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            pnr = sb.toString();
        } while (bookingRepository.findByBookingReference(pnr).isPresent());
        return pnr;
    }
}
