package com.flightmatrix.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flightmatrix.enums.PaymentMethod;
import com.flightmatrix.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private String bookingReference;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    private LocalDateTime transactionDate;

    @JsonIgnore
    private String cardNumber;

    @JsonIgnore
    private String iban;

    public Payment(Booking booking, double amount, PaymentMethod method) {
        this.booking = booking;
        this.bookingReference = booking.getBookingReference();
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
    }

    public boolean processPayment() {
        // Delegates to PaymentProcessor implementations via PaymentService
        return false;
    }

    public boolean validatePaymentDetails() {
        return amount != null && amount > 0 && method != null;
    }

    public void updateStatus(PaymentStatus newStatus) {
        this.status = newStatus;
        if (newStatus == PaymentStatus.PAID) {
            this.transactionDate = LocalDateTime.now();
        }
    }
}
