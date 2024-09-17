package com.example.coffeeApp.repo;

import com.example.coffeeApp.entity.Drink;
import org.springframework.data.repository.CrudRepository;

public interface DrinkRepository extends CrudRepository<Drink, Long> {

}
