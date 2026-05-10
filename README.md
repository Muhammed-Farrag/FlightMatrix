# FlightMatrix

Flight Booking Management System — CSE015 Object-Oriented Programming  
Alamein International University

---

## Setup & Run

**Prerequisites:** Java 17+, Maven 3.8+

```bash
# Clone / open project, then:
mvn spring-boot:run
```

The app starts on **http://localhost:8080**. SQLite database (`flightmatrix.db`) is created automatically at the project root and seeded with sample data on first launch.

---

## Sample Credentials

| Role          | Username    | Password   |
|---------------|-------------|------------|
| Administrator | `admin`     | `admin2024`|
| Agent         | `agent01`   | `agent2024`|
| Customer      | `traveler22`| `trip2023` |

---

## Seeded Flights

| Flight  | Route                        | Economy  | Business  | First Class |
|---------|------------------------------|----------|-----------|-------------|
| FM-101  | Cairo (CAI) → London (LHR)   | $517.50  | $1,430.00 | $4,075.00   |
| FM-202  | Cairo (CAI) → Dubai (DXB)    | $372.00  | $912.50   | $2,465.00   |
| FM-303  | Cairo (CAI) → New York (JFK) | $993.00  | $2,810.00 | $6,950.00   |
| FM-404  | Cairo (CAI) → Alexandria (HBE)| $84.00  | $210.00   | $472.50     |

> Prices shown include taxes: International = base × 1.15 + $50; Domestic = base × 1.05

---

## User Flows

### Customer
1. Login at `/` → redirected to `/dashboard-customer.html`
2. Search flights at `/flights-search.html`
3. Click **Book** → `/booking-create.html?flightId=N`
4. Fill passenger details, select seat class → **Confirm Booking**
5. View booking at `/booking-detail.html?ref=FM-XXXXXX` → **Pay Now**
6. Complete payment at `/payment.html?ref=FM-XXXXXX`
7. View e-ticket at `/eticket.html?ref=FM-XXXXXX`

### Agent
1. Login → `/dashboard-agent.html`
2. Access all flights and create bookings on behalf of customers (supply Customer ID)

### Admin
1. Login → `/dashboard-admin.html`
2. Manage users at `/admin-users.html` (activate / deactivate accounts)

---

## Tech Stack

| Layer    | Technology                                       |
|----------|--------------------------------------------------|
| Backend  | Spring Boot 3.2.5, Java 17                       |
| Database | SQLite via `sqlite-jdbc` + Hibernate JPA         |
| Security | Spring Security — session-based, no JWT          |
| Frontend | Pure HTML / CSS / JS (no framework)              |
| Icons    | Lucide Icons                                     |
| Fonts    | Plus Jakarta Sans · Inter · JetBrains Mono       |

---

## OOP Principles Demonstrated

| Principle     | Where                                                                 |
|---------------|-----------------------------------------------------------------------|
| Abstraction   | `User` (abstract), `Flight` (abstract), `PaymentProcessor` interface |
| Inheritance   | `Customer`, `Agent`, `Administrator` extend `User`; `DomesticFlight`, `InternationalFlight` extend `Flight` |
| Polymorphism  | `calculatePrice(SeatClass)` overridden per flight type; `PaymentProcessor` strategy pattern |
| Encapsulation | All entity fields `private`; `password` write-only; `cardNumber`/`iban` `@JsonIgnore` |
| Composition   | `Booking` owns `List<Passenger>`; `BookingSystem` coordinates all services |

---

## Project Structure

```
src/main/java/com/flightmatrix/
├── entity/          User, Customer, Agent, Administrator, Flight,
│                    DomesticFlight, InternationalFlight, Booking,
│                    Passenger, Payment, Seat
├── enums/           UserRole, BookingStatus, PaymentStatus, SeatClass, PaymentMethod
├── interfaces/      PaymentProcessor
├── payment/         CreditCardProcessor, BankTransferProcessor
├── service/         BookingSystem (coordinator), UserService, FlightService,
│                    BookingService, PaymentService
├── controller/      AuthController, FlightController, BookingController,
│                    PaymentController, AdminController
├── dto/             ApiResponse, LoginRequest, RegisterRequest, BookingRequest,
│                    PassengerRequest, PaymentRequest, FlightRequest, FlightSearchRequest
├── config/          SecurityConfig, DatabaseConfig
└── repository/      UserRepository, FlightRepository, BookingRepository,
                     PassengerRepository, PaymentRepository

src/main/resources/static/
├── css/  theme.css, components.css, navbar.css
├── js/   api.js, auth.js, flights.js, booking.js, payment.js
└── *.html  (12 pages)
```

---

## API Reference

| Method | Endpoint                          | Access          | Description                  |
|--------|-----------------------------------|-----------------|------------------------------|
| POST   | `/api/auth/login`                 | Public          | Login                        |
| POST   | `/api/auth/register`              | Public          | Register as customer         |
| POST   | `/api/auth/logout`                | Authenticated   | Logout                       |
| GET    | `/api/auth/me`                    | Authenticated   | Current user info            |
| GET    | `/api/flights/search`             | Authenticated   | Search flights               |
| GET    | `/api/flights/all`                | Authenticated   | All flights                  |
| GET    | `/api/flights/{id}`               | Authenticated   | Flight by ID                 |
| POST   | `/api/flights`                    | Agent, Admin    | Add flight                   |
| PUT    | `/api/flights/{flightNumber}`     | Agent, Admin    | Update flight                |
| POST   | `/api/bookings`                   | Authenticated   | Create booking               |
| GET    | `/api/bookings/my`                | Authenticated   | My bookings                  |
| GET    | `/api/bookings/{ref}`             | Authenticated   | Booking detail               |
| PUT    | `/api/bookings/{ref}/cancel`      | Authenticated   | Cancel booking               |
| POST   | `/api/payments/process`           | Authenticated   | Process payment              |
| GET    | `/api/payments/{ref}`             | Authenticated   | Payment status               |
| GET    | `/api/admin/users`                | Admin only      | All users                    |
| PUT    | `/api/admin/users/{id}/activate`  | Admin only      | Activate user                |
| PUT    | `/api/admin/users/{id}/deactivate`| Admin only      | Deactivate user              |
