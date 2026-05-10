package com.flightmatrix;

import com.flightmatrix.entity.*;
import com.flightmatrix.repository.FlightRepository;
import com.flightmatrix.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class FlightMatrixApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightMatrixApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(UserRepository userRepository,
                               FlightRepository flightRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                Administrator admin = new Administrator(
                    "admin",
                    passwordEncoder.encode("admin2024"),
                    "System Admin",
                    "admin@flightmatrix.com",
                    "+20-100-000-0000",
                    5
                );

                Agent agent = new Agent(
                    "agent01",
                    passwordEncoder.encode("agent2024"),
                    "John Agent",
                    "agent01@flightmatrix.com",
                    "+20-101-111-1111",
                    "Operations",
                    0.05
                );

                Customer customer = new Customer(
                    "traveler22",
                    passwordEncoder.encode("trip2023"),
                    "Jane Traveler",
                    "traveler22@email.com",
                    "+20-102-222-2222",
                    "123 Nile Street, Cairo",
                    "Window seat"
                );

                userRepository.saveAll(List.of(admin, agent, customer));
                System.out.println("[FlightMatrix] Seeded users: admin, agent01, traveler22");
            }

            if (flightRepository.count() == 0) {
                // FM-101: Cairo → London (International)
                InternationalFlight fl101 = new InternationalFlight(
                    "FM-101", "EgyptAir", "Cairo (CAI)", "London (LHR)",
                    LocalDateTime.of(2026, 6, 15, 8, 0),
                    LocalDateTime.of(2026, 6, 15, 14, 30),
                    180,
                    450.0, 1200.0, 3500.0,
                    true, ""
                );

                // FM-202: Cairo → Dubai (International)
                InternationalFlight fl202 = new InternationalFlight(
                    "FM-202", "British Airways", "Cairo (CAI)", "Dubai (DXB)",
                    LocalDateTime.of(2026, 6, 16, 11, 0),
                    LocalDateTime.of(2026, 6, 16, 15, 0),
                    200,
                    280.0, 750.0, 2100.0,
                    false, ""
                );

                // FM-303: Cairo → New York (International)
                InternationalFlight fl303 = new InternationalFlight(
                    "FM-303", "EgyptAir", "Cairo (CAI)", "New York (JFK)",
                    LocalDateTime.of(2026, 6, 17, 2, 0),
                    LocalDateTime.of(2026, 6, 17, 11, 0),
                    220,
                    820.0, 2400.0, 6000.0,
                    true, ""
                );

                // FM-404: Cairo → Alexandria (Domestic)
                DomesticFlight fl404 = new DomesticFlight(
                    "FM-404", "Air Arabia Egypt", "Cairo (CAI)", "Alexandria (HBE)",
                    LocalDateTime.of(2026, 6, 18, 9, 0),
                    LocalDateTime.of(2026, 6, 18, 10, 0),
                    100,
                    80.0, 200.0, 450.0,
                    false
                );

                flightRepository.saveAll(List.of(fl101, fl202, fl303, fl404));
                System.out.println("[FlightMatrix] Seeded 4 flights (FM-101, FM-202, FM-303, FM-404)");
            }
        };
    }
}
