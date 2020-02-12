package com.revolut.test.service;

import com.revolut.test.entity.Account;
import com.revolut.test.entity.Transaction;
import com.revolut.test.exception.InSufficientBalanceException;
import com.revolut.test.exception.InvalidAccountException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransactionServiceTest {

    Service<Transaction> service = TransactionService.getInstance();

    Service<Account> accountservice = AccountService.getInstance();

    Account account1;

    Account account2;

    Transaction transaction;

    @Before
    public void initialize() throws Exception {
        account1 = new Account(123456L, 5000.0);
        account1.setId(1);
        accountservice.create(account1);
        account2 = new Account(99999L, 5000.0);
        account2.setId(2);
        accountservice.create(account2);

        transaction = new Transaction(account1,account2,100.0);
        transaction.setId(1);
    }

    @Test
    public void createAccountTransferWithSufficientBalanceTransactionTest() throws Exception{
        service.create(transaction);
        assertEquals(4900.0, account1.getBalance(),0.0);
        assertEquals(5100.0, account2.getBalance(),0.0);
    }

    @Test(expected = InSufficientBalanceException.class)
    public void createAccountTransferWithInSufficientBalanceTransactionTest() throws Exception{
        transaction.setTransferAmt(5100.0);
        service.create(transaction);
    }

    @Test(expected = InvalidAccountException.class)
    public void createAccountTransferWithInvalidFromAccountTest() throws Exception{
        Account invalidAcc = new Account();
        invalidAcc.setId(3);
        transaction.setFromAccount(invalidAcc);
        service.create(transaction);
    }

    @Test(expected = InvalidAccountException.class)
    public void createAccountTransferWithInvalidToAccountTest() throws Exception{
        Account invalidAcc = new Account();
        invalidAcc.setId(3);
        transaction.setToAccount(invalidAcc);
        service.create(transaction);
    }

    @Test(expected = InSufficientBalanceException.class)
    public void createAccountTransferWithNegativeTransferAmtTest() throws Exception{
        transaction.setTransferAmt(-5100.0);
        service.create(transaction);
    }

}
