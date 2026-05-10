package com.flightmatrix.entity;

import com.flightmatrix.enums.SeatClass;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("DOMESTIC")
@Getter
@Setter
@NoArgsConstructor
public class DomesticFlight extends Flight {

    private Boolean roundTrip;

    // Domestic tax rate applied on top of the base price
    private static final double DOMESTIC_TAX_RATE = 0.05;

    public DomesticFlight(String flightNumber, String airline, String origin, String destination,
                          LocalDateTime departureTime, LocalDateTime arrivalTime,
                          int totalSeats, double economyPrice, double businessPrice,
                          double firstClassPrice, boolean roundTrip) {
        super(flightNumber, airline, origin, destination, departureTime, arrivalTime,
              totalSeats, economyPrice, businessPrice, firstClassPrice);
        this.roundTrip = roundTrip;
    }

    @Override
    public double calculatePrice(SeatClass seatClass) {
        double basePrice = switch (seatClass) {
            case ECONOMY     -> getEconomyPrice();
            case BUSINESS    -> getBusinessPrice();
            case FIRST_CLASS -> getFirstClassPrice();
        };
        return basePrice * (1 + DOMESTIC_TAX_RATE);
    }
}
