package com.example.coffeeApp.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Schema(description = "Кофейная машина")
public class CoffeeMachine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer coffee;
    private Integer water;
    private Integer milk;

    public CoffeeMachine() {
    }

    protected CoffeeMachine(String name, Integer coffee, Integer water, Integer milk) {
        this.name = name;
        this.coffee = coffee;
        this.water = water;
        this.milk = milk;
    }

}
