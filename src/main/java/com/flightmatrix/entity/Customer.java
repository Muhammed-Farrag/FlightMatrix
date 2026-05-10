package com.flightmatrix.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flightmatrix.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CUSTOMER")
@Getter
@Setter
@NoArgsConstructor
public class Customer extends User {

    private String address;

    private String preferences;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookingHistory = new ArrayList<>();

    public Customer(String username, String password, String name, String email,
                    String contactInfo, String address, String preferences) {
        super(username, password, name, email, contactInfo, UserRole.CUSTOMER);
        this.address = address;
        this.preferences = preferences;
    }

    @Override
    public void updateProfile(String name, String email, String contactInfo) {
        setName(name);
        setEmail(email);
        setContactInfo(contactInfo);
    }

    public List<Booking> viewBookings() {
        return bookingHistory;
    }

    public void cancelBooking(Booking booking) {
        booking.cancel();
    }
}
