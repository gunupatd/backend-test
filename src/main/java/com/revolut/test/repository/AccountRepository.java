package com.revolut.test.repository;

import com.revolut.test.entity.Account;
import com.revolut.test.entity.Entity;

public class AccountRepository<T extends Entity> extends Repository<Account>{

    private static final AccountRepository<Account> accountRepository = new AccountRepository<>();

    private AccountRepository(){

    }

    public static AccountRepository<Account> getInstance(){
        return accountRepository;
    }

}
