package com.flightmatrix.controller;

import com.flightmatrix.dto.ApiResponse;
import com.flightmatrix.dto.BookingRequest;
import com.flightmatrix.entity.Booking;
import com.flightmatrix.entity.User;
import com.flightmatrix.enums.UserRole;
import com.flightmatrix.service.BookingService;
import com.flightmatrix.service.BookingSystem;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingSystem bookingSystem;
    private final BookingService bookingService;

    public BookingController(BookingSystem bookingSystem, BookingService bookingService) {
        this.bookingSystem  = bookingSystem;
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createBooking(@RequestBody BookingRequest req,
                                                        @AuthenticationPrincipal User currentUser) {
        try {
            // Agents can supply an explicit customerId; customers always book for themselves
            Long targetId = (currentUser.getRole() != UserRole.CUSTOMER && req.getCustomerId() != null)
                    ? req.getCustomerId()
                    : currentUser.getUserId();
            Booking booking = bookingSystem.createBooking(req, targetId);
            return ResponseEntity.status(201).body(ApiResponse.success("Booking created", booking));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{reference}")
    public ResponseEntity<ApiResponse<?>> getBooking(@PathVariable String reference,
                                                     @AuthenticationPrincipal User currentUser) {
        try {
            Booking booking = bookingService.getBookingByReference(reference);
            if (currentUser.getRole() == UserRole.CUSTOMER
                    && !booking.getCustomer().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }
            return ResponseEntity.ok(ApiResponse.success(booking));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<?>> myBookings(@AuthenticationPrincipal User currentUser) {
        List<Booking> bookings = bookingService.getBookingsForCustomer(currentUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @PutMapping("/{reference}/cancel")
    public ResponseEntity<ApiResponse<?>> cancelBooking(@PathVariable String reference,
                                                        @AuthenticationPrincipal User currentUser) {
        try {
            Booking booking = bookingService.getBookingByReference(reference);
            if (currentUser.getRole() == UserRole.CUSTOMER
                    && !booking.getCustomer().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }
            Booking cancelled = bookingSystem.cancelBooking(reference);
            return ResponseEntity.ok(ApiResponse.success("Booking cancelled", cancelled));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{reference}/itinerary")
    public ResponseEntity<ApiResponse<?>> itinerary(@PathVariable String reference,
                                                    @AuthenticationPrincipal User currentUser) {
        try {
            Booking booking = bookingService.getBookingByReference(reference);
            if (currentUser.getRole() == UserRole.CUSTOMER
                    && !booking.getCustomer().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }
            return ResponseEntity.ok(ApiResponse.success(bookingSystem.generateTicket(reference)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }
}
