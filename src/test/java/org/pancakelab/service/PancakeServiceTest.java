package org.pancakelab.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.pancakelab.exception.OrderNotFoundException;
import org.pancakelab.model.Order;
import org.pancakelab.model.OrderStatus;
import org.pancakelab.model.pancakes.Chocolate;
import org.pancakelab.model.pancakes.Pancake;
import org.pancakelab.model.pancakes.Topping;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PancakeServiceTest {
    private final IPancakeService pancakeService = new PancakeService();
    private Order order = null;
    private UUID darkChocolatePancakeId;
    private UUID milkChocolatePancakeId;
    private UUID milkChocolateHazelnutsPancakeId;

    @Test
    @org.junit.jupiter.api.Order(10)
    public void GivenOrderDoesNotExist_WhenCreatingOrder_ThenOrderCreatedWithCorrectData_Test() {
        // setup

        // exercise
        order = pancakeService.createOrder(10, 20);

        assertEquals(10, order.getBuilding());
        assertEquals(20, order.getRoom());
        assertEquals(OrderStatus.CREATED, order.getStatus());

        // verify

        // tear down
    }

    @Test
    @org.junit.jupiter.api.Order(20)
    public void GivenOrderExists_WhenAddingPancakes_ThenCorrectNumberOfPancakesAdded_Test() {
        // setup

        // exercise
        addPancakes();

        // verify
        List<Pancake> ordersPancakes = pancakeService.viewOrder(order.getId());
        assertEquals(3, ordersPancakes.size());

        // tear down
    }

    @Test
    @org.junit.jupiter.api.Order(30)
    public void GivenPancakesExists_WhenRemovingPancakes_ThenCorrectNumberOfPancakesRemoved_Test() {
        // setup

        // exercise
        pancakeService.removePancake(order.getId(), darkChocolatePancakeId);
        pancakeService.removePancake(order.getId(), milkChocolatePancakeId);
        pancakeService.removePancake(order.getId(), milkChocolateHazelnutsPancakeId);

        // verify
        List<Pancake> ordersPancakes = pancakeService.viewOrder(order.getId());
        assertTrue(ordersPancakes.isEmpty());

        // tear down
    }

    @Test
    @org.junit.jupiter.api.Order(40)
    public void GivenOrderExists_WhenCompletingOrder_ThenOrderCompleted_Test() {
        // setup

        // exercise
        pancakeService.completeOrder(order.getId());

        // verify
        List<Order> completedOrders = pancakeService.listCompletedOrders();
        assertTrue(completedOrders.stream().anyMatch(o -> o.getId().equals(order.getId())));
        assertEquals(OrderStatus.COMPLETED, order.getStatus());

        // tear down
    }

    @Test
    @org.junit.jupiter.api.Order(50)
    public void GivenOrderExists_WhenPreparingOrder_ThenOrderPrepared_Test() {
        // setup

        // exercise
        pancakeService.prepareOrder(order.getId());

        // verify
        List<Order> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.stream().anyMatch(o -> o.getId().equals(order.getId())));

        List<Order> preparedOrders = pancakeService.listPreparedOrders();
        assertTrue(preparedOrders.stream().anyMatch(o -> o.getId().equals(order.getId())));
        assertEquals(OrderStatus.PREPARED, order.getStatus());

        // tear down
    }

    @Test
    @org.junit.jupiter.api.Order(60)
    public void GivenOrderExists_WhenDeliveringOrder_ThenCorrectOrderReturnedAndOrderRemovedFromTheDatabase_Test() {
        // setup
        List<String> pancakesToDeliver = pancakeService.viewOrder(order.getId()).stream()
                .map(Pancake::getDescription)
                .toList();

        // exercise
        DeliveryInfo deliveryInfo = pancakeService.deliverOrder(order.getId());

        // verify
        List<Order> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.stream().anyMatch(o -> o.getId().equals(order.getId())));

        List<Order> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.stream().anyMatch(o -> o.getId().equals(order.getId())));

        assertThrows(OrderNotFoundException.class, () -> pancakeService.viewOrder(order.getId()));

        assertEquals(order.getId(), deliveryInfo.getOrder().getId());
        assertEquals(pancakesToDeliver, deliveryInfo.getPancakeDescriptions());
        assertEquals(OrderStatus.DELIVERED, deliveryInfo.getOrder().getStatus());

        // tear down
        order = null;
    }

    @Test
    @org.junit.jupiter.api.Order(70)
    public void GivenOrderExists_WhenCancellingOrder_ThenOrderAndPancakesRemoved_Test() {
        // setup
        order = pancakeService.createOrder(10, 20);
        addPancakes();

        // exercise
        pancakeService.cancelOrder(order.getId());

        // verify
        List<Order> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.stream().anyMatch(o -> o.getId().equals(order.getId())));

        List<Order> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.stream().anyMatch(o -> o.getId().equals(order.getId())));

        assertThrows(OrderNotFoundException.class, () -> pancakeService.viewOrder(order.getId()));

        // tear down
    }

    @Test
    @org.junit.jupiter.api.Order(80)
    public void GivenOrderDoesNotExist_WhenViewingOrder_ThenThrowsException_Test() {
        // setup
        UUID nonExistentOrderId = UUID.randomUUID();

        // exercise & verify
        assertThrows(OrderNotFoundException.class, () -> pancakeService.viewOrder(nonExistentOrderId));
    }

    @Test
    @org.junit.jupiter.api.Order(90)
    public void GivenOrderInWrongState_WhenCompletingOrder_ThenThrowsException_Test() {
        // setup
        Order newOrder = pancakeService.createOrder(1, 1);
        pancakeService.completeOrder(newOrder.getId());

        // exercise & verify
        assertThrows(IllegalStateException.class, () -> pancakeService.completeOrder(newOrder.getId()));
    }

    @Test
    @org.junit.jupiter.api.Order(100)
    public void GivenOrderInWrongState_WhenPreparingOrder_ThenThrowsException_Test() {
        // setup
        Order newOrder = pancakeService.createOrder(1, 1);

        // exercise & verify
        assertThrows(IllegalStateException.class, () -> pancakeService.prepareOrder(newOrder.getId()));
    }

    @Test
    @org.junit.jupiter.api.Order(110)
    public void GivenOrderInWrongState_WhenDeliveringOrder_ThenThrowsException_Test() {
        // setup
        Order newOrder = pancakeService.createOrder(1, 1);
        pancakeService.completeOrder(newOrder.getId());

        // exercise & verify
        assertThrows(IllegalStateException.class, () -> pancakeService.deliverOrder(newOrder.getId()));
    }

    @Test
    @org.junit.jupiter.api.Order(120)
    public void GivenOrderWithInvalidBuilding_WhenCreatingOrder_ThenThrowsException_Test() {
        // exercise & verify
        assertThrows(IllegalArgumentException.class, () -> pancakeService.createOrder(0, 1));
        assertThrows(IllegalArgumentException.class, () -> pancakeService.createOrder(-1, 1));
    }

    @Test
    @org.junit.jupiter.api.Order(130)
    public void GivenOrderWithInvalidRoom_WhenCreatingOrder_ThenThrowsException_Test() {
        // exercise & verify
        assertThrows(IllegalArgumentException.class, () -> pancakeService.createOrder(1, 0));
        assertThrows(IllegalArgumentException.class, () -> pancakeService.createOrder(1, -1));
    }

    @Test
    @org.junit.jupiter.api.Order(140)
    public void GivenPancakeWithoutName_WhenBuildingPancake_ThenThrowsException_Test() {
        // setup
        Order newOrder = pancakeService.createOrder(1, 1);

        // exercise & verify
        assertThrows(IllegalStateException.class, () -> 
            new Pancake.Builder()
                .addIngredient(new Chocolate(Chocolate.Type.DARK))
                .build());
    }

    @Test
    @org.junit.jupiter.api.Order(150)
    public void GivenPancakeWithoutIngredients_WhenBuildingPancake_ThenThrowsException_Test() {
        // setup
        Order newOrder = pancakeService.createOrder(1, 1);

        // exercise & verify
        assertThrows(IllegalStateException.class, () -> 
            new Pancake.Builder()
                .setName("Empty Pancake")
                .build());
    }

    private void addPancakes() {
        // Create dark chocolate pancake
        Pancake darkChocolatePancake = new Pancake.Builder()
                .setName("Dark Chocolate Pancake")
                .addIngredient(new Chocolate(Chocolate.Type.DARK))
                .build();
        pancakeService.addPancake(order.getId(), darkChocolatePancake);
        darkChocolatePancakeId = darkChocolatePancake.getId();

        // Create milk chocolate pancake
        Pancake milkChocolatePancake = new Pancake.Builder()
                .setName("Milk Chocolate Pancake")
                .addIngredient(new Chocolate(Chocolate.Type.MILK))
                .build();
        pancakeService.addPancake(order.getId(), milkChocolatePancake);
        milkChocolatePancakeId = milkChocolatePancake.getId();

        // Create milk chocolate with hazelnuts pancake
        Pancake milkChocolateHazelnutsPancake = new Pancake.Builder()
                .setName("Milk Chocolate Hazelnuts Pancake")
                .addIngredient(new Chocolate(Chocolate.Type.MILK))
                .addIngredient(new Topping(Topping.Type.HAZELNUTS))
                .build();
        pancakeService.addPancake(order.getId(), milkChocolateHazelnutsPancake);
        milkChocolateHazelnutsPancakeId = milkChocolateHazelnutsPancake.getId();
    }
}
