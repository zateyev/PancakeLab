package org.pancakelab.model.pancakes;

public class Chocolate extends BasicIngredient {
    public enum Type {
        DARK("Dark Chocolate", 1.5),
        MILK("Milk Chocolate", 1.0);

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

    public Chocolate(Type type) {
        super(type.getName(), type.getPrice());
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
