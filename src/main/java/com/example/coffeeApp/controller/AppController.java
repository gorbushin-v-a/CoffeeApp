package com.example.coffeeApp.controller;

import com.example.coffeeApp.config.Dictionary;
import com.example.coffeeApp.entity.CoffeeMachine;
import com.example.coffeeApp.service.CoffeeMachineService;
import com.example.coffeeApp.service.DrinkService;
import com.example.coffeeApp.entity.Drink;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Tag(name = "AppController", description = "Основной и единственный контроллер приложения")
@RestController
@Slf4j
public final class AppController {

    @Autowired
    private DrinkService drinkService;
    @Autowired
    private CoffeeMachineService coffeeMachineService;
    private boolean firstStart = true;
    private final long coffeeMachineId = 1L;
    private String logText;

    @Operation(
            summary = "Отрисовка основной страницы"
    )
    @GetMapping("/")
    public ModelAndView home(ModelMap model) {
        List<Drink> listDrink = drinkService.listAll();
        listDrink.sort(Comparator.comparingInt(o -> o.getOrders() * -1));
        model.addAttribute("listDrink", listDrink);
        CoffeeMachine coffeeMachine = coffeeMachineService.get(coffeeMachineId);
        model.addAttribute("coffeeMachine", coffeeMachine);
        if (firstStart) {
            logText = Dictionary.getString("message.start");
            firstStart = false;
        }
        model.addAttribute("logText", logText);
        return new ModelAndView(Dictionary.getString("template.index"), model);
    }

    @Operation(
            summary = "Новый напиток",
            description = "Переход на форму создания рецепта нового напитка"
    )
    @GetMapping("/new")
    public ModelAndView newDrinkForm(Map<String, Object> model) {
        Drink drink = new Drink();
        model.put("drink", drink);
        return new ModelAndView(Dictionary.getString("template.newDrink"), model);
    }

    @Operation(
            summary = "Новый напиток",
            description = "Метод создания рецепта нового напитка"
    )
    @PostMapping("/new")
    public ModelAndView saveDrink(@ModelAttribute Drink drink, RedirectAttributes redirectAttrs) {
        logText = logText + drinkService.saveDrink(drink);
        redirectAttrs.addFlashAttribute("logText", logText);
        return new ModelAndView("redirect:/");
    }

    @Operation(
            summary = "Удаление напитка"
    )
    @GetMapping("/delete")
    public ModelAndView deleteDrink(@RequestParam long id, RedirectAttributes redirectAttrs) {
        drinkService.delete(id);
        logText = logText + "\n" + Dictionary.getString("message.delRecipe");
        redirectAttrs.addFlashAttribute("logText", logText);
        return new ModelAndView("redirect:/");
    }

    @Operation(
            summary = "Заказ напитка"
    )
    @GetMapping("/order")
    public ModelAndView orderDrink(@RequestParam long id, RedirectAttributes redirectAttrs) {
        CoffeeMachine coffeeMachine = coffeeMachineService.get(coffeeMachineId);
        logText = logText + drinkService.orderDrink(id, coffeeMachine);
        redirectAttrs.addFlashAttribute("logText", logText);
        return new ModelAndView("redirect:/");
    }

    @Operation(
            summary = "Добавление кофе в машину"
    )
    @PostMapping("/updateCoffee")
    public ModelAndView addCoffee(@ModelAttribute CoffeeMachine element, RedirectAttributes redirectAttrs) {
        logText = logText + coffeeMachineService.addCoffee(element, coffeeMachineId);
        redirectAttrs.addFlashAttribute("logText", logText);
        return new ModelAndView("redirect:/");
    }

    @Operation(
            summary = "Добавление воды в машину"
    )
    @PostMapping("/updateWater")
    public ModelAndView addWater(@ModelAttribute CoffeeMachine element, RedirectAttributes redirectAttrs) {
        logText = logText + coffeeMachineService.addWater(element, coffeeMachineId);
        redirectAttrs.addFlashAttribute("logText", logText);
        return new ModelAndView("redirect:/");
    }

    @Operation(
            summary = "Добавление молока в машину"
    )
    @PostMapping("/updateMilk")
    public ModelAndView addMilk(@ModelAttribute CoffeeMachine element, RedirectAttributes redirectAttrs) {
        logText = logText + coffeeMachineService.addMilk(element, coffeeMachineId);
        redirectAttrs.addFlashAttribute("logText", logText);
        return new ModelAndView("redirect:/");
    }

}
