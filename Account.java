package atm.model;

/**
 * Account - represents a bank account linked to a user.
 */
public class Account {
    private int    accountId;
    private int    userId;
    private String accountNo;
    private double balance;
    private String accountType;

    public Account(int accountId, int userId, String accountNo, double balance, String accountType) {
        this.accountId   = accountId;
        this.userId      = userId;
        this.accountNo   = accountNo;
        this.balance     = balance;
        this.accountType = accountType;
    }

    public int    getAccountId()   { return accountId; }
    public int    getUserId()      { return userId; }
    public String getAccountNo()   { return accountNo; }
    public double getBalance()     { return balance; }
    public String getAccountType() { return accountType; }

    public void setBalance(double balance) { this.balance = balance; }
}
