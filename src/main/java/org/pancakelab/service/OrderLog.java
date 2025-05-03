package org.pancakelab.service;

import org.pancakelab.model.Order;
import org.pancakelab.model.pancakes.Pancake;

import java.util.List;
import java.util.UUID;

public class OrderLog {
    private static final StringBuilder log = new StringBuilder();

    public static void logAddPancake(Order order, Pancake pancake, List<Pancake> pancakes) {
        log.append("Added pancake with description '%s' ".formatted(pancake.getDescription()))
                .append("to order %s containing %d pancakes, ".formatted(order.getId(), pancakes.size()))
                .append("for building %d, room %d.".formatted(order.getBuilding(), order.getRoom()));
    }

    public static void logRemovePancakes(Order order, UUID pancakeId, List<Pancake> pancakes) {
        log.append("Removed pancake with id '%s' ".formatted(pancakeId))
                .append("from order %s now containing %d pancakes, ".formatted(order.getId(), pancakes.size()))
                .append("for building %d, room %d.".formatted(order.getBuilding(), order.getRoom()));
    }

    public static void logCancelOrder(Order order, List<Pancake> pancakes) {
        log.append("Cancelled order %s with %d pancakes ".formatted(order.getId(), pancakes.size()))
                .append("for building %d, room %d.".formatted(order.getBuilding(), order.getRoom()));
    }

    public static void logDeliverOrder(Order order, List<Pancake> pancakes) {
        log.append("Order %s with %d pancakes ".formatted(order.getId(), pancakes.size()))
                .append("for building %d, room %d out for delivery.".formatted(order.getBuilding(), order.getRoom()));
    }
}
