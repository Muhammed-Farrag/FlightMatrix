package com.flightmatrix.interfaces;

import com.flightmatrix.entity.Payment;
import com.flightmatrix.enums.PaymentStatus;

public interface PaymentProcessor {
    boolean processPayment(Payment payment);
    boolean validatePaymentDetails(Payment payment);
    void updateStatus(Payment payment, PaymentStatus status);
}
