package org.pancakelab.service;

import org.pancakelab.exception.OrderNotFoundException;
import org.pancakelab.model.Order;
import org.pancakelab.model.OrderStatus;
import org.pancakelab.model.pancakes.Pancake;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PancakeService implements IPancakeService {
    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();
    private final Map<UUID, List<Pancake>> orderPancakes = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public Order createOrder(int building, int room) {
        Order order = new Order(building, room);
        orders.put(order.getId(), order);
        orderPancakes.put(order.getId(), new CopyOnWriteArrayList<>());
        return order;
    }

    @Override
    public void addPancake(UUID orderId, Pancake pancake) {
        lock.writeLock().lock();
        try {
            if (!orders.containsKey(orderId)) {
                throw new OrderNotFoundException(orderId.toString());
            }
            Order order = orders.get(orderId);
            if (order.getStatus() != OrderStatus.CREATED) {
                throw new IllegalStateException("Cannot add pancakes to an order that is not in CREATED state");
            }
            OrderLog.logAddPancake(order, pancake, orderPancakes.get(orderId));
            orderPancakes.get(orderId).add(pancake);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<Pancake> viewOrder(UUID orderId) {
        lock.readLock().lock();
        try {
            if (!orders.containsKey(orderId)) {
                throw new OrderNotFoundException(orderId.toString());
            }
            return new ArrayList<>(orderPancakes.get(orderId));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void removePancake(UUID orderId, UUID pancakeId) {
        lock.writeLock().lock();
        try {
            if (!orders.containsKey(orderId)) {
                throw new OrderNotFoundException(orderId.toString());
            }
            Order order = orders.get(orderId);
            if (order.getStatus() != OrderStatus.CREATED) {
                throw new IllegalStateException("Cannot remove pancakes from an order that is not in CREATED state");
            }
            List<Pancake> pancakes = orderPancakes.get(orderId);
            pancakes.removeIf(p -> p.getId().equals(pancakeId));
            OrderLog.logRemovePancakes(order, pancakeId, pancakes);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void cancelOrder(UUID orderId) {
        lock.writeLock().lock();
        try {
            if (!orders.containsKey(orderId)) {
                throw new OrderNotFoundException(orderId.toString());
            }
            Order order = orders.get(orderId);
            order.setStatus(OrderStatus.CANCELLED);
            OrderLog.logCancelOrder(order, orderPancakes.get(orderId));
            orders.remove(orderId);
            orderPancakes.remove(orderId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void completeOrder(UUID orderId) {
        lock.writeLock().lock();
        try {
            if (!orders.containsKey(orderId)) {
                throw new OrderNotFoundException(orderId.toString());
            }
            Order order = orders.get(orderId);
            if (order.getStatus() != OrderStatus.CREATED) {
                throw new IllegalStateException("Cannot complete an order that is not in CREATED state");
            }
            order.setStatus(OrderStatus.COMPLETED);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<Order> listCompletedOrders() {
        lock.readLock().lock();
        try {
            return orders.values().stream()
                    .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void prepareOrder(UUID orderId) {
        lock.writeLock().lock();
        try {
            if (!orders.containsKey(orderId)) {
                throw new OrderNotFoundException(orderId.toString());
            }
            Order order = orders.get(orderId);
            if (order.getStatus() != OrderStatus.COMPLETED) {
                throw new IllegalStateException("Cannot prepare an order that is not in COMPLETED state");
            }
            order.setStatus(OrderStatus.PREPARED);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<Order> listPreparedOrders() {
        lock.readLock().lock();
        try {
            return orders.values().stream()
                    .filter(order -> order.getStatus() == OrderStatus.PREPARED)
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public DeliveryInfo deliverOrder(UUID orderId) {
        lock.writeLock().lock();
        try {
            if (!orders.containsKey(orderId)) {
                throw new OrderNotFoundException(orderId.toString());
            }
            Order order = orders.get(orderId);
            if (order.getStatus() != OrderStatus.PREPARED) {
                throw new IllegalStateException("Cannot deliver an order that is not in PREPARED state");
            }
            order.setStatus(OrderStatus.DELIVERED);
            List<String> pancakeDescriptions = orderPancakes.get(orderId).stream()
                    .map(Pancake::getDescription)
                    .toList();
            OrderLog.logDeliverOrder(order, orderPancakes.get(orderId));
            orders.remove(orderId);
            orderPancakes.remove(orderId);
            return new DeliveryInfo(order, pancakeDescriptions);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
