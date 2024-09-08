package com.example.demo.service;

import com.example.demo.entity.CoffeeMachine;
import com.example.demo.repo.CoffeeMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CoffeeMachineService {
    @Autowired
    CoffeeMachineRepository repo;

    public void save(CoffeeMachine drink) {
        repo.save(drink);
    }

    public List<CoffeeMachine> listAll() {
        return (List<CoffeeMachine>) repo.findAll();
    }

    public CoffeeMachine get(Long id) {
        return repo.findById(id).get();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

}
