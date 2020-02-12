package com.revolut.test.entity;

import com.revolut.test.constants.TransactionStatus;

public class Entity {

    private Integer id;

    private TransactionStatus status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}
