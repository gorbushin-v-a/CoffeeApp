package com.example.demo.service;

import com.example.demo.entity.Drink;
import com.example.demo.repo.DrinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class DrinkService {
    @Autowired
    DrinkRepository repo;

    public void save(Drink drink) {
        repo.save(drink);
    }

    public List<Drink> listAll() {
        return (List<Drink>) repo.findAll();
    }

    public Drink get(Long id) {
        return repo.findById(id).get();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

}
