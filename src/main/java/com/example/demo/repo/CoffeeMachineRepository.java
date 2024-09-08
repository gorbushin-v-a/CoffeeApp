package com.example.demo.repo;

import com.example.demo.entity.CoffeeMachine;
import org.springframework.data.repository.CrudRepository;

public interface CoffeeMachineRepository extends CrudRepository<CoffeeMachine, Long> {

}
