package atm.model;

/**
 * User - represents a card holder.
 */
public class User {
    private int userId;
    private String cardNumber;
    private String fullName;
    private boolean active;

    public User(int userId, String cardNumber, String fullName, boolean active) {
        this.userId     = userId;
        this.cardNumber = cardNumber;
        this.fullName   = fullName;
        this.active     = active;
    }

    public int    getUserId()     { return userId; }
    public String getCardNumber() { return cardNumber; }
    public String getFullName()   { return fullName; }
    public boolean isActive()     { return active; }
}
