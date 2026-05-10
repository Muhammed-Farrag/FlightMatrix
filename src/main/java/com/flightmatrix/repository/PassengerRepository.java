package com.flightmatrix.repository;

import com.flightmatrix.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    List<Passenger> findByBooking_BookingReference(String bookingReference);
}
