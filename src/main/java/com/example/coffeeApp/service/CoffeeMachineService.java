package com.example.coffeeApp.service;

import com.example.coffeeApp.config.Dictionary;
import com.example.coffeeApp.entity.CoffeeMachine;
import com.example.coffeeApp.repo.CoffeeMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CoffeeMachineService {

    @Autowired
    private CoffeeMachineRepository repo;

    private void save(CoffeeMachine machine) {
        repo.save(machine);
    }

    public CoffeeMachine get(Long id) {
        return repo.findById(id).orElse(null);
    }

    /**
     * Метод добавления кофе в кофемашину.
     * @param element данные с формы
     * @param coffeeMachineId Id кофемашины
     * @return String
     */
    public String addCoffee(CoffeeMachine element, long coffeeMachineId) {
        CoffeeMachine coffeeMachine = get(coffeeMachineId);
        if (element.getCoffee() != null && coffeeMachine.getCoffee() + element.getCoffee() >= 0) {
            coffeeMachine.setCoffee(coffeeMachine.getCoffee() + element.getCoffee());
            save(coffeeMachine);
            return "\n" + element.getCoffee() + Dictionary.getString("message.addCoffee");
        } else {
            return "\n" + Dictionary.getString("message.NECoffee");
        }
    }

    /**
     * Метод добавления воды в кофемашину.
     * @param element данные с формы
     * @param coffeeMachineId Id кофемашины
     * @return String
     */
    public String addWater(CoffeeMachine element, long coffeeMachineId) {
        CoffeeMachine coffeeMachine = get(coffeeMachineId);
        if (element.getWater() != null && coffeeMachine.getWater() + element.getWater() >= 0) {
            coffeeMachine.setWater(coffeeMachine.getWater() + element.getWater());
            save(coffeeMachine);
            return "\n" + element.getWater() + Dictionary.getString("message.addWater");
        } else {
            return "\n" + Dictionary.getString("message.NEWater");
        }
    }

    /**
     * Метод добавления молока в кофемашину.
     * @param element данные с формы
     * @param coffeeMachineId Id кофемашины
     * @return String
     */
    public String addMilk(CoffeeMachine element, long coffeeMachineId) {
        CoffeeMachine coffeeMachine = get(coffeeMachineId);
        if (element.getMilk() != null && coffeeMachine.getMilk() + element.getMilk() >= 0) {
            coffeeMachine.setMilk(coffeeMachine.getMilk() + element.getMilk());
            save(coffeeMachine);
            return "\n" + element.getMilk() + Dictionary.getString("message.addMilk");
        } else {
            return "\n" + Dictionary.getString("message.NEMilk");
        }
    }

}
