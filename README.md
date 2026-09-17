# 🏦 GlobalBank ATM System — Spring Boot REST API & Simulator

<p align="center">
  <img src="https://img.shields.io/badge/Spring_Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Java-17%2F21%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_Security-6.3-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"/>
  <img src="https://img.shields.io/badge/JWT-Stateless_Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"/>
  <img src="https://img.shields.io/badge/Swagger_OpenAPI-3.0-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"/>
  <img src="https://img.shields.io/badge/H2_Database-In--Memory-004488?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
</p>

<p align="center">
  A production-ready, enterprise-grade <strong>Spring Boot 3 REST API</strong> and <strong>Interactive Web ATM Simulator</strong> for GlobalBank, featuring <strong>stateless JWT authentication</strong>, <strong>3-attempt account lockout protection</strong>, <strong>atomic financial transactions (ACID)</strong>, <strong>CSV statement export</strong>, and <strong>OpenAPI / Swagger 3 UI</strong>.
</p>

---

## 👩‍💻 Developer

| Field        | Details                                                 |
|--------------|---------------------------------------------------------|
| **Name**     | Titiksha Gupta                                          |
| **Degree**   | B.Tech — Computer Science & Engineering                 |
| **Year**     | 4th Year                                                |
| **Project**  | GlobalBank ATM Management System (Spring Boot REST API) |

---

## 🌟 Key Enhancements Over Legacy Console App

1. ⚡ **Modern Spring Boot 3 REST API Architecture**:
   - Clean separation of concerns: Controller (`@RestController`), Service (`@Service`, `@Transactional`), Repository (Spring Data JPA), Entity (`@Entity`), and DTO validation layers (`jakarta.validation`).
2. 🔐 **Stateless Security with JWT & Dual PIN Hashing**:
   - Card Number + 4-digit PIN authentication issues cryptographically signed JWT Bearer tokens.
   - Dual password encoder supporting both legacy SHA-256 hashes and BCrypt encryption.
3. 🛡️ **Brute-Force & Lockout Protection**:
   - Card automatically locks after **3 consecutive failed PIN attempts**.
   - Admin unlock endpoint allows unblocking locked cards (`POST /api/v1/admin/cards/{cardNumber}/unlock`).
4. 🔑 **PIN Change Functionality**:
   - Authenticated cardholders can safely change their 4-digit PIN with validation against current PIN.
5. 🔄 **Atomic ACID Financial Operations**:
   - Deposit, withdrawal (multiples of $10, daily $2,000 limit), and inter-account transfers run in isolated database transactions with automated rollback on failure.
   - Recipient pre-verification API prevents accidental transfers to wrong account numbers.
6. 📊 **Transaction History, Pagination & CSV Export**:
   - Mini-statement endpoint (last 8 transactions).
   - Paginated history with filtering by date range (`startDate`, `endDate`) and transaction type (`DEPOSIT`, `WITHDRAWAL`, `TRANSFER_IN`, `TRANSFER_OUT`).
   - Downloadable CSV bank statement export.
7. 🌐 **Interactive Web ATM Simulator Kiosk**:
   - Sleek glassmorphic web UI accessible at `http://localhost:8080/` with interactive demo cards, animated card slot, tactile PIN keypad, cash dispenser, and printable paper receipts!
8. 📖 **Swagger 3 / OpenAPI Documentation**:
   - Live interactive documentation and API testing sandbox at `http://localhost:8080/swagger-ui.html`.
9. 🗄️ **Dual Database Support**:
   - **H2 (Default)**: In-memory database pre-seeded with demo data for zero-config, instant local startup without installing MySQL.
   - **MySQL 8.0**: Dedicated profile (`application-mysql.properties`) for production database connectivity.

---

## 📁 Project Structure

```
ATM_Project/
├── pom.xml                                      ← Maven build configuration
├── src/
│   ├── main/
│   │   ├── java/com/globalbank/atm/
│   │   │   ├── AtmApplication.java              ← Spring Boot main entry point
│   │   │   ├── config/
│   │   │   │   ├── OpenApiConfig.java           ← Swagger UI & JWT security scheme
│   │   │   │   ├── SecurityConfig.java          ← Spring Security 6 filter chain & CORS
│   │   │   │   └── DataInitializer.java        ← Seeds 4 demo accounts on startup
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java          ← /api/v1/auth (login, logout, pin-change)
│   │   │   │   ├── AtmController.java           ← /api/v1/atm (balance, withdraw, deposit, transfer)
│   │   │   │   ├── TransactionController.java   ← /api/v1/transactions (statement, export)
│   │   │   │   └── AdminController.java         ← /api/v1/admin (unlock card, system stats)
│   │   │   ├── dto/
│   │   │   │   ├── request/                     ← LoginRequest, WithdrawRequest, TransferRequest, etc.
│   │   │   │   └── response/                    ← AuthResponse, AccountOverviewResponse, etc.
│   │   │   ├── entity/                          ← UserEntity, AccountEntity, TransactionEntity, AtmSessionEntity
│   │   │   ├── exception/                       ← GlobalExceptionHandler & custom exception classes
│   │   │   ├── repository/                      ← Spring Data JPA repositories
│   │   │   ├── security/                        ← JwtService, JwtAuthFilter, HybridPasswordEncoder
│   │   │   └── service/                         ← AuthService, AtmService, TransactionService
│   │   └── resources/
│   │       ├── application.properties           ← H2 in-memory default configuration
│   │       ├── application-mysql.properties     ← MySQL 8.0 profile configuration
│   │       └── static/                          ← Interactive ATM Web Simulator (HTML, CSS, JS)
│   │           ├── index.html
│   │           ├── style.css
│   │           └── app.js
│   └── test/
│       └── java/com/globalbank/atm/             ← Comprehensive automated test suite
│           ├── AtmApplicationTests.java
│           ├── AuthServiceTest.java
│           ├── AtmServiceTest.java
│           └── AtmControllerIntegrationTest.java
└── src/atm/                                     ← Original legacy console code
```

---

## 👤 Pre-Seeded Demo Accounts

All demo accounts are ready to use immediately:

| Card Number        | PIN  | Name            | Account No   | Initial Balance | Account Type |
|--------------------|------|-----------------|--------------|-----------------|--------------|
| `4001234567890001` | 1234 | James Wilson    | GB100000001  | $18,540.00      | SAVINGS      |
| `4001234567890002` | 5678 | Sarah Mitchell  | GB100000002  | $52,730.50      | SAVINGS      |
| `4001234567890003` | 9999 | Raj Patel       | GB100000003  | $8,200.00       | CURRENT      |
| `4001234567890004` | 2004 | Titiksha Gupta  | GB100000004  | $5,000.00       | SAVINGS      |

---

## 🚀 Quick Start & How to Run

### Prerequisites
- Java JDK 17, 21, or newer
- Maven 3.8+ (or use `./mvnw`)

### 1. Build and Run Tests
```bash
mvn clean test
```

### 2. Start the Spring Boot Application
```bash
mvn spring-boot:run
```

Once started, access:
- 🖥️ **Interactive Web ATM Simulator**: `http://localhost:8080/`
- ⚡ **Swagger UI API Documentation**: `http://localhost:8080/swagger-ui.html`
- 📄 **OpenAPI JSON Spec**: `http://localhost:8080/v3/api-docs`
- 🗄️ **H2 Web Console**: `http://localhost:8080/h2-console` *(JDBC URL: `jdbc:h2:mem:globalbank_atm`, User: `sa`, Password: empty)*

---

## 📡 REST API Reference

### 1. Authentication Endpoints (`/api/v1/auth`)

#### Authenticate Card & PIN
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "cardNumber": "4001234567890004",
  "pin": "2004"
}
```
**Response (200 OK):**
```json
{
  "success": true,
  "message": "Authentication successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "userId": 4,
    "fullName": "Titiksha Gupta",
    "maskedCardNumber": "**** **** **** 0004",
    "accountNo": "GB100000004",
    "accountType": "SAVINGS",
    "balance": 5000.00
  }
}
```

#### Change PIN
```http
POST /api/v1/auth/change-pin
Authorization: Bearer <TOKEN>
Content-Type: application/json

{
  "currentPin": "2004",
  "newPin": "9876",
  "confirmPin": "9876"
}
```

#### Logout & Close Session
```http
POST /api/v1/auth/logout
Authorization: Bearer <TOKEN>
```

---

### 2. ATM Banking Operations (`/api/v1/atm`)

#### Check Account Balance
```http
GET /api/v1/atm/balance
Authorization: Bearer <TOKEN>
```

#### Cash Withdrawal (multiples of $10, max $2,000/day)
```http
POST /api/v1/atm/withdraw
Authorization: Bearer <TOKEN>
Content-Type: application/json

{
  "amount": 100.00
}
```

#### Cash Deposit
```http
POST /api/v1/atm/deposit
Authorization: Bearer <TOKEN>
Content-Type: application/json

{
  "amount": 500.00
}
```

#### Verify Transfer Recipient
```http
GET /api/v1/atm/transfer/verify?target=4001234567890001
Authorization: Bearer <TOKEN>
```

#### Fund Transfer
```http
POST /api/v1/atm/transfer
Authorization: Bearer <TOKEN>
Content-Type: application/json

{
  "recipientTarget": "GB100000001",
  "amount": 250.00
}
```

---

### 3. Transaction History & Statements (`/api/v1/transactions`)

#### Get Mini-Statement (Last 8 Transactions)
```http
GET /api/v1/transactions/mini-statement
Authorization: Bearer <TOKEN>
```

#### Get Filtered Paginated Transactions
```http
GET /api/v1/transactions?type=WITHDRAWAL&page=0&size=10
Authorization: Bearer <TOKEN>
```

#### Export Statement CSV
```http
GET /api/v1/transactions/export
Authorization: Bearer <TOKEN>
```

---

### 4. Admin Endpoints (`/api/v1/admin`)

#### Unlock Card After 3 Failed Attempts
```http
POST /api/v1/admin/cards/4001234567890004/unlock
```

#### System Overview & Vault Metrics
```http
GET /api/v1/admin/overview
```

---

## 🐬 Running with MySQL 8.0 (Optional)

To run with your local MySQL server instead of the default H2 in-memory database:
1. Ensure MySQL is running on port 3306.
2. Run `database_setup.sql` in MySQL.
3. Update credentials in `src/main/resources/application-mysql.properties`.
4. Launch with the `mysql` profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

---

## 📜 License
Educational project developed by **Titiksha Gupta** | B.Tech Computer Science & Engineering.
