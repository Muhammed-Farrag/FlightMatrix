package com.flightmatrix.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "passengers")
@Getter
@Setter
@NoArgsConstructor
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long passengerId;

    @Column(nullable = false)
    private String name;

    private String passportNumber;

    private LocalDate dateOfBirth;

    private String specialRequests;

    private String seatNumber;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    public Passenger(String name, String passportNumber, LocalDate dateOfBirth, String specialRequests) {
        this.name = name;
        this.passportNumber = passportNumber;
        this.dateOfBirth = dateOfBirth;
        this.specialRequests = specialRequests;
    }

    public void updateInfo(String name, String passportNumber, String specialRequests) {
        this.name = name;
        this.passportNumber = passportNumber;
        this.specialRequests = specialRequests;
    }

    public String getPassengerDetails() {
        return String.format("Name: %s | Passport: %s | DOB: %s | Seat: %s",
                name, passportNumber, dateOfBirth, seatNumber);
    }
}
