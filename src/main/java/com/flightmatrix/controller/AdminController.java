package com.flightmatrix.controller;

import com.flightmatrix.dto.ApiResponse;
import com.flightmatrix.dto.RegisterRequest;
import com.flightmatrix.entity.User;
import com.flightmatrix.service.BookingSystem;
import com.flightmatrix.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final BookingSystem bookingSystem;

    public AdminController(UserService userService, BookingSystem bookingSystem) {
        this.userService   = userService;
        this.bookingSystem = bookingSystem;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<?>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<?>> createUser(@RequestBody RegisterRequest req) {
        try {
            User user = bookingSystem.registerUser(req);
            return ResponseEntity.status(201).body(ApiResponse.success("User created", user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<ApiResponse<?>> activateUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success("User activated", userService.toggleActive(id, true)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<ApiResponse<?>> deactivateUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success("User deactivated", userService.toggleActive(id, false)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<?>> getLogs() {
        Map<String, Object> stats = Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "status",    "System operational",
            "totalUsers", userService.getAllUsers().size()
        );
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
