package com.flightmatrix.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flightmatrix.enums.BookingStatus;
import com.flightmatrix.enums.PaymentStatus;
import com.flightmatrix.enums.SeatClass;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Passenger> passengers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatClass seatClass;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.RESERVED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private Double totalPrice;

    @JsonIgnore
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Booking(String bookingReference, Customer customer, Flight flight,
                   SeatClass seatClass, double totalPrice) {
        this.bookingReference = bookingReference;
        this.customer = customer;
        this.flight = flight;
        this.seatClass = seatClass;
        this.totalPrice = totalPrice;
        this.status = BookingStatus.RESERVED;
        this.paymentStatus = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void addPassenger(Passenger passenger) {
        passengers.add(passenger);
        passenger.setBooking(this);
    }

    public double calculateTotalPrice() {
        return flight.calculatePrice(seatClass) * passengers.size();
    }

    public void confirmBooking() {
        this.status = BookingStatus.CONFIRMED;
        this.paymentStatus = PaymentStatus.PAID;
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
        if (flight != null) {
            passengers.forEach(p -> flight.releaseSeat());
        }
    }

    public String generateItinerary() {
        return String.format(
            "PNR: %s | Flight: %s | %s → %s | Departure: %s | Passengers: %d | Class: %s | Total: $%.2f",
            bookingReference,
            flight.getFlightNumber(),
            flight.getOrigin(),
            flight.getDestination(),
            flight.getDepartureTime(),
            passengers.size(),
            seatClass,
            totalPrice
        );
    }
}
