-- ============================================
--   GLOBALBANK ATM - MySQL Database Setup
-- ============================================

CREATE DATABASE IF NOT EXISTS globalbank_atm;
USE globalbank_atm;

-- Users table (card holders)
CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    card_number   VARCHAR(16) NOT NULL UNIQUE,
    full_name     VARCHAR(100) NOT NULL,
    pin_hash      VARCHAR(64) NOT NULL,  -- SHA-256 hashed PIN
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Accounts table
CREATE TABLE IF NOT EXISTS accounts (
    account_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NOT NULL,
    account_no    VARCHAR(20) NOT NULL UNIQUE,
    balance       DECIMAL(15, 2) DEFAULT 0.00,
    account_type  ENUM('SAVINGS', 'CURRENT') DEFAULT 'SAVINGS',
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    txn_id        INT AUTO_INCREMENT PRIMARY KEY,
    account_id    INT NOT NULL,
    txn_type      ENUM('DEPOSIT','WITHDRAWAL','TRANSFER_IN','TRANSFER_OUT') NOT NULL,
    amount        DECIMAL(15, 2) NOT NULL,
    balance_after DECIMAL(15, 2) NOT NULL,
    description   VARCHAR(255),
    txn_ref       VARCHAR(20) NOT NULL UNIQUE,
    txn_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);

-- ATM sessions log
CREATE TABLE IF NOT EXISTS atm_sessions (
    session_id    INT AUTO_INCREMENT PRIMARY KEY,
    card_number   VARCHAR(16),
    login_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logout_time   TIMESTAMP NULL,
    status        ENUM('ACTIVE','CLOSED','FAILED') DEFAULT 'ACTIVE'
);

-- ============================================
--   SEED DATA - Demo Users
--   PIN: 1234 for card 4001234567890001
--   PIN: 5678 for card 4001234567890002
--   PIN: 9999 for card 4001234567890003
--   PIN: 2004 for card 4001234567890004  ← Titiksha Gupta
-- ============================================

INSERT INTO users (card_number, full_name, pin_hash) VALUES
('4001234567890001', 'James Wilson',   SHA2('1234', 256)),
('4001234567890002', 'Sarah Mitchell', SHA2('5678', 256)),
('4001234567890003', 'Raj Patel',      SHA2('9999', 256)),
('4001234567890004', 'Titiksha Gupta', SHA2('2004', 256));

INSERT INTO accounts (user_id, account_no, balance, account_type) VALUES
(1, 'GB100000001', 18540.00, 'SAVINGS'),
(2, 'GB100000002', 52730.50, 'SAVINGS'),
(3, 'GB100000003',  8200.00, 'CURRENT'),
(4, 'GB100000004',  5000.00, 'SAVINGS');

INSERT INTO transactions (account_id, txn_type, amount, balance_after, description, txn_ref) VALUES
(1, 'DEPOSIT',     3200.00, 18540.00, 'SALARY CREDIT',        'TXN000000001'),
(1, 'WITHDRAWAL',   200.00, 18340.00, 'ATM WITHDRAWAL',       'TXN000000002'),
(2, 'DEPOSIT',     1450.00, 52730.50, 'DIVIDEND CREDIT',      'TXN000000003'),
(3, 'DEPOSIT',     8200.00,  8200.00, 'INITIAL DEPOSIT',      'TXN000000004'),
(4, 'DEPOSIT',     5000.00,  5000.00, 'ACCOUNT OPENING BONUS','TXN000000005');
