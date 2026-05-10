package com.flightmatrix.service;

import com.flightmatrix.dto.RegisterRequest;
import com.flightmatrix.entity.*;
import com.flightmatrix.enums.UserRole;
import com.flightmatrix.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username '" + req.getUsername() + "' is already taken");
        }
        if (req.getEmail() != null && userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }
        validatePassword(req.getPassword());

        UserRole role = req.getRole() != null ? req.getRole() : UserRole.CUSTOMER;
        String encoded = passwordEncoder.encode(req.getPassword());

        User user = switch (role) {
            case CUSTOMER -> new Customer(
                req.getUsername(), encoded, req.getName(), req.getEmail(), req.getContactInfo(),
                req.getAddress(), req.getPreferences()
            );
            case AGENT -> new Agent(
                req.getUsername(), encoded, req.getName(), req.getEmail(), req.getContactInfo(),
                req.getDepartment() != null ? req.getDepartment() : "General",
                req.getCommission() != null ? req.getCommission() : 0.05
            );
            case ADMIN -> new Administrator(
                req.getUsername(), encoded, req.getName(), req.getEmail(), req.getContactInfo(),
                req.getSecurityLevel() != null ? req.getSecurityLevel() : 1
            );
        };
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateProfile(Long userId, String name, String email, String contactInfo) {
        User user = getUserById(userId);
        user.updateProfile(name, email, contactInfo);
        return userRepository.save(user);
    }

    public User toggleActive(Long id, boolean active) {
        User user = getUserById(id);
        user.setActive(active);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }
        if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*[0-9].*")) {
            throw new RuntimeException("Password must contain both letters and numbers");
        }
    }
}
