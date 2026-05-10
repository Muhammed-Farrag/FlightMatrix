package com.flightmatrix.payment;

import com.flightmatrix.entity.Payment;
import com.flightmatrix.enums.PaymentStatus;
import com.flightmatrix.interfaces.PaymentProcessor;
import org.springframework.stereotype.Component;

@Component
public class CreditCardProcessor implements PaymentProcessor {

    @Override
    public boolean processPayment(Payment payment) {
        if (!validatePaymentDetails(payment)) {
            return false;
        }
        updateStatus(payment, PaymentStatus.PAID);
        return true;
    }

    @Override
    public boolean validatePaymentDetails(Payment payment) {
        if (payment == null || payment.getAmount() == null || payment.getAmount() <= 0) {
            return false;
        }
        // Card number must be 16 digits
        String cardNumber = payment.getCardNumber();
        if (cardNumber == null || !cardNumber.replaceAll("\\s", "").matches("\\d{16}")) {
            return false;
        }
        return true;
    }

    @Override
    public void updateStatus(Payment payment, PaymentStatus status) {
        payment.setStatus(status);
    }
}
