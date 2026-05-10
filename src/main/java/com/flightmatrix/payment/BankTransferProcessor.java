package com.flightmatrix.payment;

import com.flightmatrix.entity.Payment;
import com.flightmatrix.enums.PaymentStatus;
import com.flightmatrix.interfaces.PaymentProcessor;
import org.springframework.stereotype.Component;

@Component
public class BankTransferProcessor implements PaymentProcessor {

    @Override
    public boolean processPayment(Payment payment) {
        if (!validatePaymentDetails(payment)) {
            return false;
        }
        // Bank transfer is initially pending until bank confirms
        updateStatus(payment, PaymentStatus.PENDING);
        return true;
    }

    @Override
    public boolean validatePaymentDetails(Payment payment) {
        if (payment == null || payment.getAmount() == null || payment.getAmount() <= 0) {
            return false;
        }
        // IBAN must be provided for bank transfer
        String iban = payment.getIban();
        if (iban == null || iban.trim().length() < 15) {
            return false;
        }
        return true;
    }

    @Override
    public void updateStatus(Payment payment, PaymentStatus status) {
        payment.setStatus(status);
    }
}
