package com.example.demo.repo;

import com.example.demo.entity.Drink;
import org.springframework.data.repository.CrudRepository;

public interface DrinkRepository extends CrudRepository<Drink, Long> {

}
