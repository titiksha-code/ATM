package com.globalbank.atm.dto.response;

public class RecipientVerificationResponse {

    private boolean valid;
    private String recipientName;
    private String recipientAccountNo;
    private String maskedCardNumber;
    private String message;

    public RecipientVerificationResponse() {
    }

    public RecipientVerificationResponse(boolean valid, String recipientName, String recipientAccountNo,
                                         String maskedCardNumber, String message) {
        this.valid = valid;
        this.recipientName = recipientName;
        this.recipientAccountNo = recipientAccountNo;
        this.maskedCardNumber = maskedCardNumber;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientAccountNo() {
        return recipientAccountNo;
    }

    public void setRecipientAccountNo(String recipientAccountNo) {
        this.recipientAccountNo = recipientAccountNo;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
