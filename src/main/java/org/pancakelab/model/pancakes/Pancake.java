package org.pancakelab.model.pancakes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Pancake {
    private final UUID id;
    private final List<Ingredient> ingredients;
    private final String name;

    private Pancake(Builder builder) {
        this.id = UUID.randomUUID();
        this.ingredients = new ArrayList<>(builder.ingredients);
        this.name = builder.name;
    }

    public UUID getId() {
        return id;
    }

    public List<Ingredient> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return name + " with " + String.join(", ", ingredients.stream()
                .map(Ingredient::getName)
                .toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pancake pancake = (Pancake) o;
        return Objects.equals(id, pancake.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static class Builder {
        private UUID orderId;
        private final List<Ingredient> ingredients = new ArrayList<>();
        private String name;

        public Builder() {
        }

        public Builder addIngredient(Ingredient ingredient) {
            ingredients.add(ingredient);
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Pancake build() {
            if (name == null || name.isBlank()) {
                throw new IllegalStateException("Pancake name must be set");
            }
            if (ingredients.isEmpty()) {
                throw new IllegalStateException("Pancake must have at least one ingredient");
            }
            return new Pancake(this);
        }
    }

    @Override
    public String toString() {
        return "Pancake{" +
                "id=" + id +
                ", ingredients=" + ingredients +
                ", name='" + name + '\'' +
                '}';
    }
}
