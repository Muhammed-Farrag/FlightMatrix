package com.flightmatrix.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "flights")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "flight_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String flightNumber;

    @Column(nullable = false)
    private String airline;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    private Integer totalSeats;

    private Integer availableSeats;

    // Base prices per class — subclasses apply their own multipliers in calculatePrice()
    private Double economyPrice;
    private Double businessPrice;
    private Double firstClassPrice;

    @JsonIgnore
    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> seats = new ArrayList<>();

    protected Flight(String flightNumber, String airline, String origin, String destination,
                     LocalDateTime departureTime, LocalDateTime arrivalTime,
                     int totalSeats, double economyPrice, double businessPrice, double firstClassPrice) {
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.economyPrice = economyPrice;
        this.businessPrice = businessPrice;
        this.firstClassPrice = firstClassPrice;
    }

    public abstract double calculatePrice(SeatClass seatClass);

    // Computed price getters — polymorphic: each subclass applies its own tax logic
    public double getCalculatedEconomyPrice()    { return calculatePrice(SeatClass.ECONOMY); }
    public double getCalculatedBusinessPrice()   { return calculatePrice(SeatClass.BUSINESS); }
    public double getCalculatedFirstClassPrice() { return calculatePrice(SeatClass.FIRST_CLASS); }

    public String getFlightType() {
        return this instanceof DomesticFlight ? "DOMESTIC" : "INTERNATIONAL";
    }

    public void updateSchedule(LocalDateTime departure, LocalDateTime arrival) {
        this.departureTime = departure;
        this.arrivalTime = arrival;
    }

    public boolean checkAvailability(int requestedSeats) {
        return availableSeats != null && availableSeats >= requestedSeats;
    }

    public void reserveSeat() {
        if (availableSeats != null && availableSeats > 0) {
            availableSeats--;
        }
    }

    public void releaseSeat() {
        if (availableSeats != null && totalSeats != null && availableSeats < totalSeats) {
            availableSeats++;
        }
    }
}
