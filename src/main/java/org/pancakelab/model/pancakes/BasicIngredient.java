package org.pancakelab.model.pancakes;

public abstract class BasicIngredient implements Ingredient {
    private final String name;
    private final double price;

    protected BasicIngredient(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPrice() {
        return price;
    }
}
