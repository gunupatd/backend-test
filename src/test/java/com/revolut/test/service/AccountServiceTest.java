package com.revolut.test.service;

import com.revolut.test.entity.Account;
import com.revolut.test.exception.InvalidAccountException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccountServiceTest {

    Service<Account> service = AccountService.getInstance();

    Account account;

    @Before
    public void initialize() throws Exception {
        account = new Account(123456L, 5000.0);
        account.setId(1);
        service.create(account);
    }

    @Test(expected = Test.None.class)
    public void createTest() throws Exception {
        service.create(account);
    }

    @Test
    public void updateTest() throws InvalidAccountException{
        account.setBalance(5100.0);
        service.update(account);
        Account expectedAccount = service.findById(account.getId());
        assertEquals(5100.0, expectedAccount.getBalance(),0.0);
    }

    @Test
    public void findByIdTest() throws InvalidAccountException{
        assertEquals(account.getId(),service.findById(account.getId()).getId());
    }

    @Test(expected = InvalidAccountException.class)
    public void findByIdNotAvailableTest() throws InvalidAccountException{
        service.findById(3).getId();
    }

    @Test
    public void findAllTest(){
        assertEquals(1,service.findAll().size());
    }
}
