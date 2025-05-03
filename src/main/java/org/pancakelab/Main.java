package org.pancakelab;

import org.pancakelab.model.Order;
import org.pancakelab.model.pancakes.Chocolate;
import org.pancakelab.model.pancakes.Pancake;
import org.pancakelab.model.pancakes.Topping;
import org.pancakelab.service.DeliveryInfo;
import org.pancakelab.service.IPancakeService;
import org.pancakelab.service.PancakeService;

public class Main {
    public static void main(String[] args) {
        IPancakeService service = new PancakeService();

        // Create an order
        Order order = service.createOrder(1, 101);

        // Create a custom pancake using the Builder pattern
        Pancake darkChocolatePancake = new Pancake.Builder()
                .setName("Dark Chocolate Delight")
                .addIngredient(new Chocolate(Chocolate.Type.DARK))
                .addIngredient(new Topping(Topping.Type.WHIPPED_CREAM))
                .addIngredient(new Topping(Topping.Type.HAZELNUTS))
                .build();

        // Add the pancake to the order
        service.addPancake(order.getId(), darkChocolatePancake);

        // View the order
        System.out.println("Order contents:");
        service.viewOrder(order.getId()).forEach(System.out::println);

        // Complete the order
        service.completeOrder(order.getId());

        // Prepare the order
        service.prepareOrder(order.getId());

        // Deliver the order
        DeliveryInfo deliveryInfo = service.deliverOrder(order.getId());
        System.out.println("\nDelivered to: Building " + deliveryInfo.getOrder().getBuilding() +
                ", Room " + deliveryInfo.getOrder().getRoom());
        System.out.println("Items delivered:");
        deliveryInfo.getPancakeDescriptions().forEach(System.out::println);
    }
}
