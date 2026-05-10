package com.flightmatrix.controller;

import com.flightmatrix.dto.ApiResponse;
import com.flightmatrix.dto.PaymentRequest;
import com.flightmatrix.entity.Payment;
import com.flightmatrix.service.BookingSystem;
import com.flightmatrix.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final BookingSystem bookingSystem;
    private final PaymentService paymentService;

    public PaymentController(BookingSystem bookingSystem, PaymentService paymentService) {
        this.bookingSystem  = bookingSystem;
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<ApiResponse<?>> processPayment(@RequestBody PaymentRequest req) {
        try {
            Payment payment = bookingSystem.processPayment(req);
            return ResponseEntity.ok(ApiResponse.success("Payment processed", payment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{reference}")
    public ResponseEntity<ApiResponse<?>> getStatus(@PathVariable String reference) {
        try {
            Payment payment = paymentService.getPaymentStatus(reference);
            return ResponseEntity.ok(ApiResponse.success(payment));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }
}
