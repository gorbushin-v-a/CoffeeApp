package com.example.demo.service;

import com.example.demo.config.Dictionary;
import com.example.demo.entity.CoffeeMachine;
import com.example.demo.entity.Drink;
import com.example.demo.repo.DrinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@Slf4j
public class DrinkService {

    @Autowired
    private DrinkRepository repo;
    private boolean makeOrder = false;

    private void save(Drink drink) {
        repo.save(drink);
    }

    private Drink get(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<Drink> listAll() {
        return (List<Drink>) repo.findAll();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    /**
     * Метод добавления рецепта напитка на уровне сервиса.
     * @param drink напиток
     * @return String
     */
    public String saveDrink(Drink drink) {
        if (drink.getName() == null || drink.getCoffee() == null || drink.getWater() == null
                || drink.getMilk() == null || drink.getCooking_time() == null) {
            return "\n" + Dictionary.getString("message.needFillFields");
        } else if (drink.getCoffee() < 0 || drink.getWater() < 0
                || drink.getMilk() < 0 || drink.getCooking_time() < 0) {
            return "\n" + Dictionary.getString("message.negativeFields");
        } else {
            drink.setOrders(0);
            save(drink);
            return "\n" + drink.getName() + Dictionary.getString("message.addRecipe");
        }
    }

    /**
     * Метод заказа напитка на уровне сервиса.
     * @param id id напитка
     * @param coffeeMachine кофемашина, которая будет выполнять операцию
     * @return String
     */
    public String orderDrink(long id, CoffeeMachine coffeeMachine) {
        if (makeOrder) {
            return "\n" + Dictionary.getString("message.busyMachine");
        } else {
            Drink producedDrink = get(id);
            if (producedDrink.getCoffee() > coffeeMachine.getCoffee()
                    || producedDrink.getWater() > coffeeMachine.getWater()
                    || producedDrink.getMilk() > coffeeMachine.getMilk()) {
                return "\n" + Dictionary.getString("message.NEI");
            } else {
                makeOrder = true;
                coffeeMachine.setCoffee(coffeeMachine.getCoffee() - producedDrink.getCoffee());
                coffeeMachine.setWater(coffeeMachine.getWater() - producedDrink.getWater());
                coffeeMachine.setMilk(coffeeMachine.getMilk() - producedDrink.getMilk());

                try {
                    Thread.sleep(producedDrink.getCooking_time());
                } catch (InterruptedException e) {
                    log.error("InterruptedException", e);
                }
                producedDrink.setOrders(producedDrink.getOrders() + 1);
                save(producedDrink);
                makeOrder = false;

                return "\n" + producedDrink.getName() + Dictionary.getString("message.drinkReady");
            }
        }
    }

}
