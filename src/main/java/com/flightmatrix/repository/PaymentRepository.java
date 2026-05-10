package com.flightmatrix.repository;

import com.flightmatrix.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingReference(String bookingReference);
    Optional<Payment> findByBooking_Id(Long bookingId);
}
