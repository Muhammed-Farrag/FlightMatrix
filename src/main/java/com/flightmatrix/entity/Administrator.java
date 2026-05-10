package com.flightmatrix.entity;

import com.flightmatrix.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@NoArgsConstructor
public class Administrator extends User {

    private Integer securityLevel;

    public Administrator(String username, String password, String name, String email,
                         String contactInfo, Integer securityLevel) {
        super(username, password, name, email, contactInfo, UserRole.ADMIN);
        this.securityLevel = securityLevel;
    }

    @Override
    public void updateProfile(String name, String email, String contactInfo) {
        setName(name);
        setEmail(email);
        setContactInfo(contactInfo);
    }

    public void modifySystemSettings(String key, String value) {
        // System settings management — implemented in AdminService
    }

    public void manageUserAccess(User user, boolean enable) {
        // User access control — implemented in AdminService
    }
}
