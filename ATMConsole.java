package atm;

import atm.model.Transaction;
import atm.service.ATMService;
import atm.util.ATMUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * ATMConsole - Main entry point. Handles all console UI and user interaction.
 *
 * HOW TO RUN:
 *   1. Set up MySQL using database_setup.sql
 *   2. Update DBConnection.java with your MySQL credentials
 *   3. Add mysql-connector-java.jar to your classpath
 *   4. Compile and run: javac -cp .;mysql-connector-java.jar atm/ATMConsole.java
 *                        java  -cp .;mysql-connector-java.jar atm.ATMConsole
 */
public class ATMConsole {

    private static final ATMService service = new ATMService();
    private static final Scanner    scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();
        while (true) {
            showWelcomeScreen();
        }
    }

    // ─── Welcome / Login ────────────────────────────────────────────

    static void showWelcomeScreen() {
        ATMUtil.thickDivider();
        System.out.println(ATMUtil.center("╔══ GLOBALBANK ATM ══╗", 50));
        System.out.println(ATMUtil.center("INTERNATIONAL BANKING SERVICES", 50));
        ATMUtil.thickDivider();
        System.out.println();
        System.out.println("  Please enter your Card Number (or 'quit' to exit):");
        System.out.print("  Card No ▶  ");

        String cardNumber = scanner.nextLine().trim();
        if (cardNumber.equalsIgnoreCase("quit")) {
            System.out.println("\n  Thank you for using GlobalBank ATM. Goodbye!\n");
            System.exit(0);
        }

        System.out.print("  PIN     ▶  ");
        String pin = scanner.nextLine().trim();

        System.out.println();
        System.out.println("  Authenticating...");

        try {
            boolean ok = service.login(cardNumber, pin);
            if (!ok) {
                printError("Invalid card number or PIN. Please try again.");
                pause(1500);
                return;
            }
            printSuccess("Authentication successful!");
            pause(800);
            showMainMenu();

        } catch (IllegalStateException e) {
            printError("Your card has been blocked. Please contact customer support.");
            pause(2000);
        } catch (SQLException e) {
            printError("Database error: " + e.getMessage());
            pause(2000);
        }
    }

    // ─── Main Menu ──────────────────────────────────────────────────

    static void showMainMenu() {
        while (service.isLoggedIn()) {
            System.out.println();
            ATMUtil.thickDivider();
            System.out.printf("  Welcome, %-34s%n", service.getUserName());
            System.out.printf("  Card: %-36s%n", service.getMaskedCard());
            System.out.printf("  Account: %-34s%n", service.getAccountNo() + "  [" + service.getAccountType() + "]");
            ATMUtil.divider();
            System.out.println("  SELECT AN OPTION:");
            System.out.println();
            System.out.println("   [1]  Balance Enquiry");
            System.out.println("   [2]  Cash Withdrawal");
            System.out.println("   [3]  Deposit Funds");
            System.out.println("   [4]  Fund Transfer");
            System.out.println("   [5]  Mini Statement");
            System.out.println("   [6]  Change PIN");
            System.out.println("   [0]  Logout");
            System.out.println();
            ATMUtil.divider();
            System.out.print("  Choice ▶  ");

            String choice = scanner.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1" -> doBalance();
                case "2" -> doWithdraw();
                case "3" -> doDeposit();
                case "4" -> doTransfer();
                case "5" -> doMiniStatement();
                case "6" -> doChangePin();
                case "0" -> {
                    doLogout();
                    return;
                }
                default  -> printError("Invalid option. Please select 1–6 or 0.");
            }
        }
    }

    // ─── Balance Enquiry ────────────────────────────────────────────

    static void doBalance() {
        ATMUtil.thickDivider();
        System.out.println("  BALANCE ENQUIRY");
        ATMUtil.divider();
        System.out.printf("  Account No   : %s%n", service.getAccountNo());
        System.out.printf("  Account Type : %s%n", service.getAccountType());
        System.out.println();
        System.out.printf("  %-28s%n", "AVAILABLE BALANCE:");
        System.out.printf("  %s%n", ATMUtil.formatMoney(service.getBalance()));
        System.out.println();
        System.out.printf("  As of: %s%n", ATMUtil.now());
        ATMUtil.thickDivider();
        pressEnterToContinue();
    }

    // ─── Withdrawal ─────────────────────────────────────────────────

    static void doWithdraw() {
        ATMUtil.thickDivider();
        System.out.println("  CASH WITHDRAWAL");
        ATMUtil.divider();
        System.out.println("  Quick amounts: $20  $50  $100  $200  $500  $1000");
        System.out.printf("  Available balance: %s%n", ATMUtil.formatMoney(service.getBalance()));
        System.out.println();
        System.out.print("  Enter amount ▶  $");

        String input = scanner.nextLine().trim();
        if (input.isEmpty() || input.equalsIgnoreCase("cancel")) return;

        try {
            double amount = Double.parseDouble(input);
            service.withdraw(amount);
            System.out.println();
            printSuccess("Withdrawal Successful!");
            ATMUtil.divider();
            System.out.printf("  Amount Dispensed : %s%n", ATMUtil.formatMoney(amount));
            System.out.printf("  Remaining Balance: %s%n", ATMUtil.formatMoney(service.getBalance()));
            System.out.printf("  Date / Time      : %s%n", ATMUtil.now());
            System.out.println();
            System.out.println("  Please collect your cash.");
            printReceipt("WITHDRAWAL", amount);

        } catch (NumberFormatException e) {
            printError("Invalid amount entered.");
        } catch (IllegalArgumentException e) {
            printError(e.getMessage());
        } catch (SQLException e) {
            printError("Transaction failed: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    // ─── Deposit ────────────────────────────────────────────────────

    static void doDeposit() {
        ATMUtil.thickDivider();
        System.out.println("  DEPOSIT FUNDS");
        ATMUtil.divider();
        System.out.println("  Insert cash into the deposit slot.");
        System.out.printf("  Current balance: %s%n", ATMUtil.formatMoney(service.getBalance()));
        System.out.println();
        System.out.print("  Enter deposit amount ▶  $");

        String input = scanner.nextLine().trim();
        if (input.isEmpty() || input.equalsIgnoreCase("cancel")) return;

        try {
            double amount = Double.parseDouble(input);
            service.deposit(amount);
            System.out.println();
            printSuccess("Deposit Successful!");
            ATMUtil.divider();
            System.out.printf("  Amount Deposited : %s%n", ATMUtil.formatMoney(amount));
            System.out.printf("  New Balance      : %s%n", ATMUtil.formatMoney(service.getBalance()));
            System.out.printf("  Date / Time      : %s%n", ATMUtil.now());
            printReceipt("DEPOSIT", amount);

        } catch (NumberFormatException e) {
            printError("Invalid amount entered.");
        } catch (IllegalArgumentException e) {
            printError(e.getMessage());
        } catch (SQLException e) {
            printError("Transaction failed: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    // ─── Fund Transfer ──────────────────────────────────────────────

    static void doTransfer() {
        ATMUtil.thickDivider();
        System.out.println("  FUND TRANSFER");
        ATMUtil.divider();
        System.out.printf("  Your balance: %s%n", ATMUtil.formatMoney(service.getBalance()));
        System.out.println();
        System.out.print("  Recipient Card Number ▶  ");
        String recipientCard = scanner.nextLine().trim();
        if (recipientCard.isEmpty() || recipientCard.equalsIgnoreCase("cancel")) return;

        System.out.print("  Transfer Amount       ▶  $");
        String input = scanner.nextLine().trim();
        if (input.isEmpty() || input.equalsIgnoreCase("cancel")) return;

        try {
            double amount = Double.parseDouble(input);

            // Confirm
            System.out.println();
            System.out.printf("  Confirm transfer of %s to card %s? [Y/N]: ",
                    ATMUtil.formatMoney(amount), ATMUtil.maskCard(recipientCard));
            String confirm = scanner.nextLine().trim();
            if (!confirm.equalsIgnoreCase("Y")) {
                System.out.println("  Transfer cancelled.");
                pressEnterToContinue();
                return;
            }

            String recipientName = service.transfer(recipientCard, amount);
            System.out.println();
            printSuccess("Transfer Successful!");
            ATMUtil.divider();
            System.out.printf("  Transferred To   : %s (%s)%n", recipientName, ATMUtil.maskCard(recipientCard));
            System.out.printf("  Amount           : %s%n", ATMUtil.formatMoney(amount));
            System.out.printf("  Your Balance     : %s%n", ATMUtil.formatMoney(service.getBalance()));
            System.out.printf("  Reference        : TXN%d%n", System.currentTimeMillis() % 100000000);
            System.out.printf("  Date / Time      : %s%n", ATMUtil.now());
            printReceipt("TRANSFER", amount);

        } catch (NumberFormatException e) {
            printError("Invalid amount entered.");
        } catch (IllegalArgumentException e) {
            printError(e.getMessage());
        } catch (SQLException e) {
            printError("Transaction failed: " + e.getMessage());
        }
        pressEnterToContinue();
    }

    // ─── Mini Statement ─────────────────────────────────────────────

    static void doMiniStatement() {
        ATMUtil.thickDivider();
        System.out.println("  MINI STATEMENT — Last 8 Transactions");
        ATMUtil.divider();
        System.out.printf("  %-12s %-22s %-14s %s%n", "TYPE", "DESCRIPTION", "AMOUNT", "DATE");
        ATMUtil.divider();

        try {
            List<Transaction> txns = service.getMiniStatement();
            if (txns.isEmpty()) {
                System.out.println("  No transactions found.");
            } else {
                for (Transaction t : txns) {
                    String sign = t.getTxnType().contains("OUT") || t.getTxnType().equals("WITHDRAWAL") ? "-" : "+";
                    String desc = t.getDescription().length() > 20
                                ? t.getDescription().substring(0, 20) : t.getDescription();
                    System.out.printf("  %-12s %-22s %s%-12s %s%n",
                            t.getTxnType().replace("_", " "),
                            desc,
                            sign,
                            ATMUtil.formatMoney(t.getAmount()),
                            t.getTxnDate().substring(0, 10));
                }
            }
            ATMUtil.divider();
            System.out.printf("  Closing Balance: %s%n", ATMUtil.formatMoney(service.getBalance()));

        } catch (SQLException e) {
            printError("Could not retrieve statement: " + e.getMessage());
        }
        ATMUtil.thickDivider();
        pressEnterToContinue();
    }

    // ─── Change PIN ─────────────────────────────────────────────────

    static void doChangePin() {
        ATMUtil.thickDivider();
        System.out.println("  CHANGE PIN");
        ATMUtil.divider();
        System.out.println("  (Feature available — connect to UserDAO.updatePin())");
        System.out.println("  Contact your bank branch for PIN changes.");
        ATMUtil.thickDivider();
        pressEnterToContinue();
    }

    // ─── Logout ─────────────────────────────────────────────────────

    static void doLogout() {
        try {
            service.logout();
        } catch (SQLException e) {
            System.err.println("Session close error: " + e.getMessage());
        }
        System.out.println();
        ATMUtil.thickDivider();
        System.out.println("  Thank you for banking with GlobalBank.");
        System.out.println("  Please collect your card.");
        System.out.printf("  Session ended: %s%n", ATMUtil.now());
        ATMUtil.thickDivider();
        System.out.println();
        pause(1500);
    }

    // ─── Receipt Printer ────────────────────────────────────────────

    static void printReceipt(String type, double amount) {
        System.out.println();
        System.out.println("  ┌────────────────────────────────┐");
        System.out.println("  │       GLOBALBANK RECEIPT       │");
        System.out.println("  ├────────────────────────────────┤");
        System.out.printf ("  │  %-30s  │%n", "Date: " + ATMUtil.now().substring(0, 11));
        System.out.printf ("  │  %-30s  │%n", "Card: " + service.getMaskedCard());
        System.out.println("  ├────────────────────────────────┤");
        System.out.printf ("  │  %-30s  │%n", type + ": " + ATMUtil.formatMoney(amount));
        System.out.printf ("  │  %-30s  │%n", "Balance: " + ATMUtil.formatMoney(service.getBalance()));
        System.out.println("  ├────────────────────────────────┤");
        System.out.printf ("  │  %-30s  │%n", "Ref: TXN" + (System.currentTimeMillis() % 100000000));
        System.out.println("  │    Thank you for banking!      │");
        System.out.println("  └────────────────────────────────┘");
    }

    // ─── Helpers ────────────────────────────────────────────────────

    static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════╗");
        System.out.println("  ║         G L O B A L B A N K   A T M         ║");
        System.out.println("  ║        International Banking Services        ║");
        System.out.println("  ║          Java + MySQL · Real System          ║");
        System.out.println("  ╚══════════════════════════════════════════════╝");
        System.out.println();
        pause(1000);
    }

    static void printError(String msg) {
        System.out.println();
        System.out.println("  ✖  ERROR: " + msg);
        System.out.println();
    }

    static void printSuccess(String msg) {
        System.out.println("  ✔  " + msg);
    }

    static void pressEnterToContinue() {
        System.out.println();
        System.out.print("  Press ENTER to return to menu...");
        scanner.nextLine();
    }

    static void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
