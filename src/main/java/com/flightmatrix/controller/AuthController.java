package com.flightmatrix.controller;

import com.flightmatrix.dto.ApiResponse;
import com.flightmatrix.dto.LoginRequest;
import com.flightmatrix.dto.RegisterRequest;
import com.flightmatrix.entity.User;
import com.flightmatrix.enums.UserRole;
import com.flightmatrix.service.BookingSystem;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final BookingSystem bookingSystem;

    public AuthController(AuthenticationManager authenticationManager, BookingSystem bookingSystem) {
        this.authenticationManager = authenticationManager;
        this.bookingSystem = bookingSystem;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest req,
                                                HttpServletRequest httpRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            User user = (User) auth.getPrincipal();
            return ResponseEntity.ok(ApiResponse.success("Login successful", user));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid username or password"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody RegisterRequest req) {
        try {
            req.setRole(UserRole.CUSTOMER); // public registration always creates customers
            User user = bookingSystem.registerUser(req);
            return ResponseEntity.status(201).body(ApiResponse.success("Registration successful", user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> me(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}
