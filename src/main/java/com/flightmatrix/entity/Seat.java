package com.flightmatrix.entity;

import com.flightmatrix.enums.SeatClass;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @Column(nullable = false)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatClass seatClass;

    private boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    public Seat(String seatNumber, SeatClass seatClass, Flight flight) {
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.flight = flight;
        this.available = true;
    }

    public boolean reserve() {
        if (!available) return false;
        available = false;
        return true;
    }

    public void release() {
        available = true;
    }
}
