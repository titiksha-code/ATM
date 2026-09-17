package com.globalbank.atm.exception;

public class AtmExceptions {

    public static class AtmException extends RuntimeException {
        public AtmException(String message) {
            super(message);
        }
    }

    public static class CardNotFoundException extends AtmException {
        public CardNotFoundException(String message) {
            super(message);
        }
    }

    public static class CardLockedException extends AtmException {
        public CardLockedException(String message) {
            super(message);
        }
    }

    public static class CardBlockedException extends AtmException {
        public CardBlockedException(String message) {
            super(message);
        }
    }

    public static class InvalidPinException extends AtmException {
        public InvalidPinException(String message) {
            super(message);
        }
    }

    public static class InsufficientFundsException extends AtmException {
        public InsufficientFundsException(String message) {
            super(message);
        }
    }

    public static class DailyLimitExceededException extends AtmException {
        public DailyLimitExceededException(String message) {
            super(message);
        }
    }

    public static class InvalidAmountException extends AtmException {
        public InvalidAmountException(String message) {
            super(message);
        }
    }

    public static class SelfTransferException extends AtmException {
        public SelfTransferException(String message) {
            super(message);
        }
    }

    public static class AccountNotFoundException extends AtmException {
        public AccountNotFoundException(String message) {
            super(message);
        }
    }
}
