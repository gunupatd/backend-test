package com.revolut.test.entity;

public class Account extends Entity{

    private Long mobileNo;

    private Double balance;

    public Account(Long mobileNo, Double balance) {
        this.mobileNo = mobileNo;
        this.balance = balance;
    }

    public Account() {
    }

    public Long getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(Long mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

}
