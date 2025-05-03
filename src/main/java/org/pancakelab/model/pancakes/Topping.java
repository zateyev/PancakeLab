package org.pancakelab.model.pancakes;

public class Topping extends BasicIngredient {
    public enum Type {
        WHIPPED_CREAM("Whipped Cream", 0.5),
        HAZELNUTS("Hazelnuts", 0.8),
        STRAWBERRIES("Strawberries", 1.0);

        private final String name;
        private final double price;

        Type(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }

    private final Type type;

    public Topping(Type type) {
        super(type.getName(), type.getPrice());
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
