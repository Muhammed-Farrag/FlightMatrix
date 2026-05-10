package com.flightmatrix.service;

import com.flightmatrix.dto.PaymentRequest;
import com.flightmatrix.entity.Booking;
import com.flightmatrix.entity.Payment;
import com.flightmatrix.enums.PaymentMethod;
import com.flightmatrix.enums.PaymentStatus;
import com.flightmatrix.interfaces.PaymentProcessor;
import com.flightmatrix.payment.BankTransferProcessor;
import com.flightmatrix.payment.CreditCardProcessor;
import com.flightmatrix.repository.BookingRepository;
import com.flightmatrix.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final CreditCardProcessor creditCardProcessor;
    private final BankTransferProcessor bankTransferProcessor;

    public PaymentService(PaymentRepository paymentRepository,
                          BookingRepository bookingRepository,
                          CreditCardProcessor creditCardProcessor,
                          BankTransferProcessor bankTransferProcessor) {
        this.paymentRepository    = paymentRepository;
        this.bookingRepository    = bookingRepository;
        this.creditCardProcessor  = creditCardProcessor;
        this.bankTransferProcessor = bankTransferProcessor;
    }

    public Payment processPayment(PaymentRequest req) {
        Booking booking = bookingRepository.findByBookingReference(req.getBookingReference())
                .orElseThrow(() -> new RuntimeException("Booking not found: " + req.getBookingReference()));

        if (booking.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Booking is already paid");
        }

        double amount = (req.getAmount() != null && req.getAmount() > 0)
                ? req.getAmount()
                : booking.getTotalPrice();
        Payment payment = new Payment(booking, amount, req.getMethod());
        payment.setCardNumber(req.getCardNumber());
        payment.setIban(req.getIban());

        PaymentProcessor processor = req.getMethod() == PaymentMethod.CREDIT_CARD
                ? creditCardProcessor
                : bankTransferProcessor;

        if (!processor.validatePaymentDetails(payment)) {
            throw new RuntimeException("Invalid payment details for method: " + req.getMethod());
        }

        boolean success = processor.processPayment(payment);
        payment = paymentRepository.save(payment);

        // Credit card payments confirm immediately; bank transfers stay RESERVED until bank confirms
        if (success && req.getMethod() == PaymentMethod.CREDIT_CARD) {
            booking.confirmBooking();
            bookingRepository.save(booking);
        }

        return payment;
    }

    @Transactional(readOnly = true)
    public Payment getPaymentStatus(String bookingReference) {
        return paymentRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new RuntimeException("No payment found for: " + bookingReference));
    }
}
