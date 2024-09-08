package com.example.demo.controller;

import com.example.demo.entity.CoffeeMachine;
import com.example.demo.service.CoffeeMachineService;
import com.example.demo.service.DrinkService;
import com.example.demo.entity.Drink;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Tag(name="AppController", description="Основной и единственный контроллер приложения")
@RestController
public class AppController {
    @Autowired
    private DrinkService drinkService;
    @Autowired
    private CoffeeMachineService coffeeMachineService;
    private boolean firstStart = true;
    private String log;
    private boolean makeOrder = false;

    @Operation(
            summary = "Отрисовка основной страницы"
    )
    @GetMapping("/")
    public ModelAndView home(ModelMap model) {
        List<Drink> listDrink = drinkService.listAll();
        listDrink.sort(Comparator.comparingInt(o -> o.getOrders()*-1));
        model.addAttribute("listDrink", listDrink);
        CoffeeMachine coffeeMachine = coffeeMachineService.get(1L);
        model.addAttribute("coffeeMachine", coffeeMachine);
        if (firstStart) {
            log = "Кофейная машина приветствует Вас";
            firstStart = false;
        }
        model.addAttribute("logText", log);
        return new ModelAndView("index", model);
    }

    @Operation(
            summary = "Новый напиток",
            description = "Переход на форму создания рецепта нового напитка"
    )
    @GetMapping("/new")
    public ModelAndView newDrinkForm(Map<String, Object> model) {
        Drink drink = new Drink();
        model.put("drink", drink);
        return new ModelAndView("new_drink", model);
    }

    @Operation(
            summary = "Новый напиток",
            description = "Метод создания рецепта нового напитка"
    )
    @PostMapping("/new")
    public ModelAndView saveDrink(@ModelAttribute Drink drink, RedirectAttributes redirectAttrs) {
        if (drink.getName() == null || drink.getCoffee() == null || drink.getWater() == null
                || drink.getMilk() == null || drink.getCooking_time() == null) {
            log = log + "\nНеобходимо заполнить все поля в рецепте";
            redirectAttrs.addFlashAttribute("logText", log);
        } else if (drink.getCoffee() < 0 || drink.getWater() < 0
                || drink.getMilk() < 0 || drink.getCooking_time() < 0) {
            log = log + "\nРецепт не может иметь отрицательные значения";
            redirectAttrs.addFlashAttribute("logText", log);
        } else {
            drink.setOrders(0);
            drinkService.save(drink);
            log = log + "\nРецепт " + drink.getName() + " добавлен";
            redirectAttrs.addFlashAttribute("logText", log);
        }
        return new ModelAndView("redirect:/");
    }

    @Operation(
            summary = "Удаление напитка"
    )
    @GetMapping("/delete")
    public ModelAndView deleteDrink(@RequestParam long id, RedirectAttributes redirectAttrs) {
        drinkService.delete(id);
        log = log + "\nНапиток удалён!";
        redirectAttrs.addFlashAttribute("logText", log);
        return new ModelAndView("redirect:/");
    }

    @Operation(
            summary = "Заказ напитка"
    )
    @GetMapping("/order")
    public ModelAndView orderDrink(@RequestParam long id, RedirectAttributes redirectAttrs) {
        if (makeOrder) {
            log = log + "\nКофейная машина занята!";
            redirectAttrs.addFlashAttribute("logText", log);
            return new ModelAndView("redirect:/");
        } else {
            Drink producedDrink = drinkService.get(id);
            CoffeeMachine coffeeMachine = coffeeMachineService.get(1L);
            if (producedDrink.getCoffee() > coffeeMachine.getCoffee()
                    || producedDrink.getWater() > coffeeMachine.getWater()
                    || producedDrink.getMilk() > coffeeMachine.getMilk()) {
                log = log + "\nНедостаточно ингредиентов!";
                redirectAttrs.addFlashAttribute("logText", log);
                return new ModelAndView("redirect:/");
            } else {
                makeOrder = true;
                coffeeMachine.setCoffee(coffeeMachine.getCoffee() - producedDrink.getCoffee());
                coffeeMachine.setWater(coffeeMachine.getWater() - producedDrink.getWater());
                coffeeMachine.setMilk(coffeeMachine.getMilk() - producedDrink.getMilk());
                log = log + "\nНапиток " + producedDrink.getName() + " начал готовиться";
                redirectAttrs.addFlashAttribute("logText", log);

                Callable<ModelAndView> myCallable = () -> {
                    try {
                        Thread.sleep(producedDrink.getCooking_time());
                    } catch (InterruptedException ignored) {}
                    producedDrink.setOrders(producedDrink.getOrders()+1);
                    drinkService.save(producedDrink);
                    makeOrder = false;
                    log = log + "\nНапиток " + producedDrink.getName() + " готов";
                    redirectAttrs.addFlashAttribute("logText", log);
                    return new ModelAndView("redirect:/");
                };

                try {
                    myCallable.call();
                } catch (Exception ignored) {}
                return new ModelAndView("redirect:/");
            }
        }
    }

    @Operation(
            summary = "Добавление кофе в машину"
    )
    @PostMapping("/updateCoffee")
    public ModelAndView addCoffee(@ModelAttribute CoffeeMachine element, RedirectAttributes redirectAttrs) {
        if (element.getCoffee() != null) {
            CoffeeMachine coffeeMachine = coffeeMachineService.get(1L);
            coffeeMachine.setCoffee(coffeeMachine.getCoffee() + element.getCoffee());
            coffeeMachineService.save(coffeeMachine);
            log = log + "\nДобавлено " + element.getCoffee() + " мг кофе";
            redirectAttrs.addFlashAttribute("logText", log);
        }
        return new ModelAndView("redirect:/");
    }

    @Operation(
            summary = "Добавление воды в машину"
    )
    @PostMapping("/updateWater")
    public ModelAndView addWater(@ModelAttribute CoffeeMachine element, RedirectAttributes redirectAttrs) {
        if (element.getWater() != null) {
            CoffeeMachine coffeeMachine = coffeeMachineService.get(1L);
            coffeeMachine.setWater(coffeeMachine.getWater() + element.getWater());
            coffeeMachineService.save(coffeeMachine);
            log = log + "\nДобавлено " + element.getWater() + " мг воды";
            redirectAttrs.addFlashAttribute("logText", log);
        }
        return new ModelAndView("redirect:/");
    }

    @Operation(
            summary = "Добавление молока в машину"
    )
    @PostMapping("/updateMilk")
    public ModelAndView addMilk(@ModelAttribute CoffeeMachine element, RedirectAttributes redirectAttrs) {
        CoffeeMachine coffeeMachine = coffeeMachineService.get(1L);
        if (element.getMilk() != null && coffeeMachine.getMilk() + element.getMilk() >= 0) {
            coffeeMachine.setMilk(coffeeMachine.getMilk() + element.getMilk());
            coffeeMachineService.save(coffeeMachine);
            log = log + "\nДобавлено " + element.getMilk() + " мг молока";
            redirectAttrs.addFlashAttribute("logText", log);
        } else {
            log = log + "\nВведите число такое, чтобы в кофемашине не было отрицательного количества молока";
            redirectAttrs.addFlashAttribute("logText", log);
        }
        return new ModelAndView("redirect:/");
    }

}
