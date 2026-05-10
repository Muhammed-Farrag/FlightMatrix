package com.flightmatrix.entity;

import com.flightmatrix.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("AGENT")
@Getter
@Setter
@NoArgsConstructor
public class Agent extends User {

    private String department;

    private Double commission;

    public Agent(String username, String password, String name, String email,
                 String contactInfo, String department, Double commission) {
        super(username, password, name, email, contactInfo, UserRole.AGENT);
        this.department = department;
        this.commission = commission;
    }

    @Override
    public void updateProfile(String name, String email, String contactInfo) {
        setName(name);
        setEmail(email);
        setContactInfo(contactInfo);
    }

    public void modifyBooking(Booking booking, String newStatus) {
        booking.setStatus(com.flightmatrix.enums.BookingStatus.valueOf(newStatus));
    }
}
