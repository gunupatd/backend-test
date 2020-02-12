package com.revolut.test.repository;

import com.revolut.test.entity.Entity;
import com.revolut.test.entity.Transaction;

public class TransactionRepository<T extends Entity> extends Repository<Transaction> {

    private static final TransactionRepository<Transaction> transactionRepository = new TransactionRepository<>();

    private TransactionRepository(){
    }

    public static TransactionRepository<Transaction> getInstance(){
        return transactionRepository;
    }

}
