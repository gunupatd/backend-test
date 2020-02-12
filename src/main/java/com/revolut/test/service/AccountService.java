package com.revolut.test.service;

import com.revolut.test.entity.Account;
import com.revolut.test.exception.InvalidAccountException;
import com.revolut.test.repository.AccountRepository;
import com.revolut.test.repository.Repository;

import java.util.List;

public class AccountService implements Service<Account>{

        private final AccountRepository<Account> accountRepository = AccountRepository.getInstance();

        private static final AccountService accountService = new AccountService();

        private AccountService(){

        }

        public static AccountService getInstance(){
            return accountService;
        }

        public void create(Account account) throws Exception {
            accountRepository.create(account);
        }

        public List<Account> findAll(){
            return accountRepository.findAll();
        }

        public Account findById(Integer id) throws InvalidAccountException {

            if(id == null)
                throw new InvalidAccountException("Invalid Account Id: "+id);

            Account account  = accountRepository.findById(id);

            if(account == null)
                throw new InvalidAccountException("Invalid Account Id: "+id);
            return account;
        }

        public void update(Account account) throws InvalidAccountException {
             findById(account.getId());
             accountRepository.update(account);
        }




}
