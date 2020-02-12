package com.revolut.test.repository;

import com.revolut.test.entity.Account;
import com.revolut.test.entity.Entity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class Repository<T extends Entity> {

    private final Map<Integer, T> table = new ConcurrentHashMap<>();

    public List<T> findAll(){
        return table.values().stream().collect(Collectors.toList());
    }

    public void create(T t){
        table.put(t.getId(),t);
    }

    public void update(T t){
        table.put(t.getId(), t);
    }

    public T findById(Integer id){
        return table.get(id);
    }

}
