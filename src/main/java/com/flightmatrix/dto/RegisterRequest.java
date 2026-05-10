package com.flightmatrix.dto;

import com.flightmatrix.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String name;
    private String email;
    private String contactInfo;
    private UserRole role; // defaults to CUSTOMER in public registration

    // Customer fields
    private String address;
    private String preferences;

    // Agent fields
    private String department;
    private Double commission;

    // Admin fields
    private Integer securityLevel;
}
