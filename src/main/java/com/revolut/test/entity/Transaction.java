package com.revolut.test.entity;

public class Transaction extends Entity {

    public Transaction(Account fromAccount, Account toAccount, Double transferAmt) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.transferAmt = transferAmt;
    }

    public Transaction(Account fromAccount, Account toAccount, Double transferAmt, String reference) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.transferAmt = transferAmt;
        this.reference = reference;
    }

    public Transaction(){

    }

    private Account fromAccount;

    private Account toAccount;

    private Double transferAmt;

    private String reference;

    public Account getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }

    public Double getTransferAmt() {
        return transferAmt;
    }

    public void setTransferAmt(Double transferAmt) {
        this.transferAmt = transferAmt;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

}
