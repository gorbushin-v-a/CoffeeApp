package com.example.demo.controller;

import com.example.demo.config.Dictionary;
import com.example.demo.entity.CoffeeMachine;
import com.example.demo.service.CoffeeMachineService;
import com.example.demo.service.DrinkService;
import com.example.demo.entity.Drink;
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
import java.util.concurrent.Callable;

@Tag(name = "AppController", description = "Основной и единственный контроллер приложения")
@RestController
@Slf4j
public final class AppController {
    @Autowired
    private DrinkService drinkService;
    @Autowired
    private CoffeeMachineService coffeeMachineService;
    private boolean firstStart = true;
    private String logText;
    private boolean makeOrder = false;

    @Operation(
            summary = "Отрисовка основной страницы"
    )
    @GetMapping("/")
    public ModelAndView home(ModelMap model) {
        List<Drink> listDrink = drinkService.listAll();
        listDrink.sort(Comparator.comparingInt(o -> o.getOrders() * -1));
        model.addAttribute("listDrink", listDrink);
        CoffeeMachine coffeeMachine = coffeeMachineService.get(1L);
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
        if (drink.getName() == null || drink.getCoffee() == null || drink.getWater() == null
                || drink.getMilk() == null || drink.getCooking_time() == null) {
            logText = logText + "\n" + Dictionary.getString("message.needFillFields");
            redirectAttrs.addFlashAttribute("logText", logText);
        } else if (drink.getCoffee() < 0 || drink.getWater() < 0
                || drink.getMilk() < 0 || drink.getCooking_time() < 0) {
            logText = logText + "\n" + Dictionary.getString("message.negativeFields");
            redirectAttrs.addFlashAttribute("logText", logText);
        } else {
            drink.setOrders(0);
            drinkService.save(drink);
            logText = logText + "\n" + drink.getName() + Dictionary.getString("message.addRecipe");
            redirectAttrs.addFlashAttribute("logText", logText);
        }
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
        if (makeOrder) {
            logText = logText + "\n" + Dictionary.getString("message.busyMachine");
            redirectAttrs.addFlashAttribute("logText", logText);
            return new ModelAndView("redirect:/");
        } else {
            Drink producedDrink = drinkService.get(id);
            CoffeeMachine coffeeMachine = coffeeMachineService.get(1L);
            if (producedDrink.getCoffee() > coffeeMachine.getCoffee()
                    || producedDrink.getWater() > coffeeMachine.getWater()
                    || producedDrink.getMilk() > coffeeMachine.getMilk()) {
                logText = logText + "\n" + Dictionary.getString("message.NEI");
                redirectAttrs.addFlashAttribute("logText", logText);
                return new ModelAndView("redirect:/");
            } else {
                makeOrder = true;
                coffeeMachine.setCoffee(coffeeMachine.getCoffee() - producedDrink.getCoffee());
                coffeeMachine.setWater(coffeeMachine.getWater() - producedDrink.getWater());
                coffeeMachine.setMilk(coffeeMachine.getMilk() - producedDrink.getMilk());
                logText = logText + "\n" + producedDrink.getName() + Dictionary.getString("message.makeDrink");
                redirectAttrs.addFlashAttribute("logText", logText);

                Callable<ModelAndView> myCallable = () -> {
                    try {
                        Thread.sleep(producedDrink.getCooking_time());
                    } catch (InterruptedException e) {
                        log.error("InterruptedException", e);
                    }
                    producedDrink.setOrders(producedDrink.getOrders() + 1);
                    drinkService.save(producedDrink);
                    makeOrder = false;
                    logText = logText + "\n" + producedDrink.getName()
                            + Dictionary.getString("message.drinkReady");
                    redirectAttrs.addFlashAttribute("logText", logText);
                    return new ModelAndView("redirect:/");
                };

                try {
                    myCallable.call();
                } catch (Exception e) {
                    log.error("Exception", e);
                }
                return new ModelAndView("redirect:/");
            }
        }
    }

    @Operation(
            summary = "Добавление кофе в машину"
    )
    @PostMapping("/updateCoffee")
    public ModelAndView addCoffee(@ModelAttribute CoffeeMachine element, RedirectAttributes redirectAttrs) {
        CoffeeMachine coffeeMachine = coffeeMachineService.get(1L);
        if (element.getCoffee() != null && coffeeMachine.getCoffee() + element.getCoffee() >= 0) {
            coffeeMachine.setCoffee(coffeeMachine.getCoffee() + element.getCoffee());
            coffeeMachineService.save(coffeeMachine);
            logText = logText + "\n" + element.getCoffee() + Dictionary.getString("message.addCoffee");
            redirectAttrs.addFlashAttribute("logText", logText);
        } else {
            logText = logText + "\n" + Dictionary.getString("message.NECoffee");
            redirectAttrs.addFlashAttribute("logText", logText);
        }
        return new ModelAndView("redirect:/");
    }

    @Operation(
            summary = "Добавление воды в машину"
    )
    @PostMapping("/updateWater")
    public ModelAndView addWater(@ModelAttribute CoffeeMachine element, RedirectAttributes redirectAttrs) {
        CoffeeMachine coffeeMachine = coffeeMachineService.get(1L);
        if (element.getWater() != null && coffeeMachine.getWater() + element.getWater() >= 0) {
            coffeeMachine.setWater(coffeeMachine.getWater() + element.getWater());
            coffeeMachineService.save(coffeeMachine);
            logText = logText + "\n" + element.getWater() + Dictionary.getString("message.addWater");
            redirectAttrs.addFlashAttribute("logText", logText);
        } else {
            logText = logText + "\n" + Dictionary.getString("message.NEWater");
            redirectAttrs.addFlashAttribute("logText", logText);
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
            logText = logText + "\n" + element.getMilk() + Dictionary.getString("message.addMilk");
            redirectAttrs.addFlashAttribute("logText", logText);
        } else {
            logText = logText + "\n" + Dictionary.getString("message.NEMilk");
            redirectAttrs.addFlashAttribute("logText", logText);
        }
        return new ModelAndView("redirect:/");
    }

}
