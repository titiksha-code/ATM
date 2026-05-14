# 🏦 GlobalBank ATM System

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql"/>
  <img src="https://img.shields.io/badge/JDBC-Driver-green?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Status-Complete-brightgreen?style=for-the-badge"/>
</p>

<p align="center">
  A fully functional console-based ATM simulation system built with <strong>Java</strong> and <strong>MySQL</strong>,
  featuring real database connectivity, SHA-256 PIN encryption, and ACID-compliant transactions.
</p>

---

## 👩‍💻 Developer

| Field        | Details                          |
|--------------|----------------------------------|
| **Name**     | Titiksha Gupta                   |
| **Degree**   | B.Tech — Computer Science        |
| **Year**     | 4th Year                         |
| **Age**      | 20                               |
| **Project**  | ATM Management System (Java + MySQL) |

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [Demo Accounts](#-demo-accounts)
- [Setup & Installation](#-setup--installation)
- [How to Run](#-how-to-run)
- [Screenshots](#-screenshots)
- [Concepts Used](#-concepts-used)

---

## ✨ Features

- 🔐 **Secure Authentication** — Card number + SHA-256 hashed PIN login
- 💰 **Balance Enquiry** — Real-time balance from MySQL database
- 💸 **Cash Withdrawal** — With validation (multiples of $10, insufficient funds check)
- 🏧 **Cash Deposit** — With maximum limit validation
- 🔄 **Fund Transfer** — Atomic transfer between two accounts with rollback on failure
- 📄 **Mini Statement** — Last 8 transactions fetched from database
- 🧾 **Receipt Generation** — Printed after every transaction
- 📝 **Session Logging** — Every login/logout stored in `atm_sessions` table
- 🔁 **ACID Transactions** — Uses JDBC `setAutoCommit` and `rollback` for data safety

---

## 🛠 Tech Stack

| Layer          | Technology                    |
|----------------|-------------------------------|
| Language       | Java 17                       |
| Database       | MySQL 8.0                     |
| DB Driver      | MySQL Connector/J (JDBC)      |
| Architecture   | MVC — Model, DAO, Service     |
| Security       | SHA-256 PIN Hashing           |
| IDE            | VS Code / IntelliJ IDEA       |
| Version Control| Git & GitHub                  |

---

## 📁 Project Structure

```
GlobalBank-ATM-Java/
│
├── 📄 database_setup.sql          ← Run in MySQL Workbench first
├── 📄 README.md
│
└── 📂 src/
    └── 📂 atm/
        │
        ├── 📄 ATMConsole.java     ← MAIN entry point (run this)
        │
        ├── 📂 model/
        │   ├── 📄 User.java
        │   ├── 📄 Account.java
        │   └── 📄 Transaction.java
        │
        ├── 📂 dao/
        │   ├── 📄 UserDAO.java    ← Login, session queries
        │   └── 📄 AccountDAO.java ← Withdraw, deposit, transfer
        │
        ├── 📂 service/
        │   └── 📄 ATMService.java ← Business logic & validation
        │
        └── 📂 util/
            ├── 📄 DBConnection.java ← MySQL connection manager
            └── 📄 ATMUtil.java      ← PIN hash, formatting helpers
```

---

## 🗄 Database Schema

```sql
users         → user_id, card_number, full_name, pin_hash, is_active
accounts      → account_id, user_id, account_no, balance, account_type
transactions  → txn_id, account_id, txn_type, amount, balance_after, description, txn_ref, txn_date
atm_sessions  → session_id, card_number, login_time, logout_time, status
```

---

## 👤 Demo Accounts

| Card Number        | PIN  | Name            | Balance     |
|--------------------|------|-----------------|-------------|
| 4001234567890001   | 1234 | James Wilson    | $18,540.00  |
| 4001234567890002   | 5678 | Sarah Mitchell  | $52,730.50  |
| 4001234567890003   | 9999 | Raj Patel       | $8,200.00   |
| 4001234567890004   | 2004 | Titiksha Gupta  | $5,000.00   |

---

## ⚙ Setup & Installation

### Prerequisites
- ✅ Java JDK 17+
- ✅ MySQL Server 8.0+
- ✅ MySQL Workbench
- ✅ VS Code with Java Extension Pack
- ✅ [mysql-connector-java.jar](https://dev.mysql.com/downloads/connector/j/)

### Step 1 — Clone the Repository
```bash
git clone https://github.com/YourUsername/GlobalBank-ATM-Java.git
cd GlobalBank-ATM-Java
```

### Step 2 — Set Up Database
Open **MySQL Workbench**, connect to localhost, open `database_setup.sql` and click ⚡ Run.

### Step 3 — Add MySQL Connector JAR
Download `mysql-connector-java-8.x.x.jar` and place it in:
```
GlobalBank-ATM-Java/
└── lib/
    └── mysql-connector-java-8.x.x.jar
```

### Step 4 — Configure DB Credentials
Open `src/atm/util/DBConnection.java` and update:
```java
private static final String DB_USER     = "root";         // your MySQL username
private static final String DB_PASSWORD = "your_password"; // your MySQL password
```

### Step 5 — Configure VS Code Classpath
Create `.vscode/settings.json` in your project root:
```json
{
  "java.project.referencedLibraries": [
    "lib/**/*.jar"
  ]
}
```

---

## ▶ How to Run

Open `src/atm/ATMConsole.java` in VS Code and click the **▶ Run** button above the `main` method.

Or compile and run manually from terminal:
```bash
# Compile
javac -cp .;lib/mysql-connector-java-8.x.x.jar src/atm/**/*.java src/atm/*.java

# Run
java -cp .;lib/mysql-connector-java-8.x.x.jar atm.ATMConsole
```

On Mac/Linux replace `;` with `:`.

---

## 🖥 Screenshots

```
  ╔══════════════════════════════════════════════╗
  ║         G L O B A L B A N K   A T M         ║
  ║        International Banking Services        ║
  ║          Java + MySQL · Real System          ║
  ╚══════════════════════════════════════════════╝

  ══════════════════════════════════════════════
          GLOBALBANK ATM  |  WELCOME
  ══════════════════════════════════════════════
  Please enter your Card Number:
  Card No ▶  4001234567890004
  PIN     ▶  ****

  ✔  Authentication successful!

  ══════════════════════════════════════════════
  Welcome, Titiksha Gupta!
  Card:    **** **** **** 0004
  Account: GB100000004  [SAVINGS]
  ──────────────────────────────────────────────
  SELECT AN OPTION:

   [1]  Balance Enquiry
   [2]  Cash Withdrawal
   [3]  Deposit Funds
   [4]  Fund Transfer
   [5]  Mini Statement
   [6]  Change PIN
   [0]  Logout
```

---

## 📚 Concepts Used

- **Object-Oriented Programming** — Classes, Inheritance, Encapsulation
- **JDBC** — Java Database Connectivity for MySQL
- **DAO Pattern** — Data Access Object separates DB logic from business logic
- **MVC Architecture** — Model, View (Console), Controller (Service)
- **Cryptography** — SHA-256 hashing for secure PIN storage
- **ACID Transactions** — Atomicity via `setAutoCommit(false)` and `rollback()`
- **Exception Handling** — Try-catch for DB and input errors
- **Collections** — `ArrayList` for transaction history
- **PreparedStatement** — Prevents SQL injection attacks

---

## 🔮 Future Enhancements

- [ ] Add PIN change functionality
- [ ] Implement account lockout after 3 wrong PIN attempts
- [ ] Add date-range transaction filtering
- [ ] Build a JavaFX GUI version
- [ ] Add OTP-based two-factor authentication
- [ ] Export statements as PDF

---

## 📜 License

This project is built for educational purposes as part of B.Tech Computer Science coursework.

---

<p align="center">
  Made with ❤️ by <strong>Titiksha Gupta</strong> | B.Tech CSE 4th Year
</p>
