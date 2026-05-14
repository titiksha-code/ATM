package atm.model;

/**
 * Transaction - represents a single bank transaction.
 */
public class Transaction {
    private int    txnId;
    private String txnType;
    private double amount;
    private double balanceAfter;
    private String description;
    private String txnRef;
    private String txnDate;

    public Transaction(int txnId, String txnType, double amount,
                       double balanceAfter, String description,
                       String txnRef, String txnDate) {
        this.txnId        = txnId;
        this.txnType      = txnType;
        this.amount       = amount;
        this.balanceAfter = balanceAfter;
        this.description  = description;
        this.txnRef       = txnRef;
        this.txnDate      = txnDate;
    }

    public int    getTxnId()        { return txnId; }
    public String getTxnType()      { return txnType; }
    public double getAmount()       { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public String getDescription()  { return description; }
    public String getTxnRef()       { return txnRef; }
    public String getTxnDate()      { return txnDate; }
}
