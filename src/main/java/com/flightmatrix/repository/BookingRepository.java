package com.flightmatrix.repository;

import com.flightmatrix.entity.Booking;
import com.flightmatrix.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByCustomer(Customer customer);
    List<Booking> findByCustomer_UserId(Long customerId);
}
