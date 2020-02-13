package com.revolut.test.service;

import com.revolut.test.constants.TransactionStatus;
import com.revolut.test.entity.Account;
import com.revolut.test.entity.Transaction;
import com.revolut.test.exception.InSufficientBalanceException;
import com.revolut.test.exception.InvalidAccountException;
import com.revolut.test.repository.Repository;
import com.revolut.test.repository.TransactionRepository;

import java.util.List;

public class TransactionService implements Service<Transaction>{

    private final Repository<Transaction> transactionRepository = TransactionRepository.getInstance();

    private final AccountService accountService = AccountService.getInstance();

    private static final TransactionService transactionService = new TransactionService();

    private TransactionService(){

    }

    public static TransactionService getInstance(){
        return transactionService;
    }


    public void create(Transaction transaction) throws Exception {

        Account fromAccount = accountService.findById(transaction.getFromAccount().getId());

        Account toAccount = accountService.findById(transaction.getToAccount().getId());

        if(fromAccount == null )
            throw new InvalidAccountException("Account No:" + transaction.getFromAccount().getId());

        if(toAccount == null)
            throw new InvalidAccountException("Account No:" + transaction.getToAccount().getId());

        if( (fromAccount.getBalance() - transaction.getTransferAmt()) < 0.0 )
            throw new InSufficientBalanceException();

        if( transaction.getTransferAmt() < 0.0 )
            throw new InSufficientBalanceException("Amount to Transfer cannot be negative");

        fromAccount.setBalance(fromAccount.getBalance() - transaction.getTransferAmt());
        toAccount.setBalance(toAccount.getBalance() + transaction.getTransferAmt());


        // Not the most efficient way to rollback. Because it is inmemory
        try {
            accountService.update(fromAccount);
            accountService.update(toAccount);
            transactionRepository.create(transaction);
        }catch(Exception exception){
            // start to rollback
            fromAccount.setBalance(fromAccount.getBalance() + transaction.getTransferAmt());
            toAccount.setBalance(toAccount.getBalance() - transaction.getTransferAmt());
            transaction.setStatus(TransactionStatus.FAILED);
            accountService.update(fromAccount);
            accountService.update(toAccount);
            transactionRepository.create(transaction);
        }

    }

    @Override
    public List<Transaction> findAll() {
        return null;
    }

    public Transaction findById(Integer id) throws InvalidAccountException {
        Transaction transaction  = transactionRepository.findById(id);
        if(transaction == null)
            throw new InvalidAccountException("Invalid Transaction Id: "+id);
        return transaction;
    }

    @Override
    public void update(Transaction transaction) throws InvalidAccountException {

    }


}
