package com.flightmatrix.dto;

import com.flightmatrix.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String bookingReference;
    private PaymentMethod method;
    private Double amount;
    private String cardNumber; // CREDIT_CARD only
    private String iban;       // BANK_TRANSFER only
}
