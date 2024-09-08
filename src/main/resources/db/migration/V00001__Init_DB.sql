--создание таблицы с рецептами напитков
DROP TABLE IF EXISTS public.drink;

CREATE TABLE public.drink
(
    id serial NOT NULL,
    name character varying(50) NOT NULL DEFAULT 'example',
    coffee integer NOT NULL DEFAULT 1,
    water integer NOT NULL DEFAULT 1,
    milk integer NOT NULL DEFAULT 1,
    cooking_time integer NOT NULL DEFAULT 120,
    orders integer NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.drink
    OWNER to postgres;

INSERT INTO drink(name, coffee, water, milk, cooking_time, orders) VALUES ('espresso', 7000, 30000, 0, 50000, 0);
INSERT INTO drink(name, coffee, water, milk, cooking_time, orders) VALUES ('americano', 7000, 90000, 0, 90000, 0);
INSERT INTO drink(name, coffee, water, milk, cooking_time, orders) VALUES ('cappuccino', 7000, 30000, 90000, 120000, 0);

--создание таблицы кофейных машин
DROP TABLE IF EXISTS public.coffee_machine;

CREATE TABLE public.coffee_machine
(
    id serial NOT NULL,
    name character varying(50) NOT NULL DEFAULT 'example',
    coffee integer NOT NULL,
    water integer NOT NULL,
    milk integer NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.coffee_machine
    OWNER to postgres;

INSERT INTO coffee_machine(name, coffee, water, milk) VALUES ('base_machine', 0, 0, 0);
