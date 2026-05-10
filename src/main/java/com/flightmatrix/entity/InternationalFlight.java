package com.flightmatrix.entity;

import com.flightmatrix.enums.SeatClass;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("INTERNATIONAL")
@Getter
@Setter
@NoArgsConstructor
public class InternationalFlight extends Flight {

    private Boolean visaRequired;

    private String transitCountries;

    // International tax rate + fixed international fee applied on top of the base price
    private static final double INTERNATIONAL_TAX_RATE = 0.15;
    private static final double INTERNATIONAL_FEE = 50.0;

    public InternationalFlight(String flightNumber, String airline, String origin, String destination,
                               LocalDateTime departureTime, LocalDateTime arrivalTime,
                               int totalSeats, double economyPrice, double businessPrice,
                               double firstClassPrice, boolean visaRequired, String transitCountries) {
        super(flightNumber, airline, origin, destination, departureTime, arrivalTime,
              totalSeats, economyPrice, businessPrice, firstClassPrice);
        this.visaRequired = visaRequired;
        this.transitCountries = transitCountries;
    }

    @Override
    public double calculatePrice(SeatClass seatClass) {
        double basePrice = switch (seatClass) {
            case ECONOMY     -> getEconomyPrice();
            case BUSINESS    -> getBusinessPrice();
            case FIRST_CLASS -> getFirstClassPrice();
        };
        return (basePrice * (1 + INTERNATIONAL_TAX_RATE)) + INTERNATIONAL_FEE;
    }
}
