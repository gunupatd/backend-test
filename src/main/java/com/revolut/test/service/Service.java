package com.revolut.test.service;

import com.revolut.test.entity.Account;
import com.revolut.test.entity.Entity;
import com.revolut.test.entity.Transaction;
import com.revolut.test.exception.InSufficientBalanceException;
import com.revolut.test.exception.InvalidAccountException;

import java.util.List;

public interface Service<T extends Entity> {

    public void create(T t) throws Exception;

    public List<T> findAll();

    public T findById(Integer id) throws InvalidAccountException ;

    public void update(T t) throws InvalidAccountException ;


}
