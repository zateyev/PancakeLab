package org.pancakelab.service;

import org.pancakelab.model.Order;
import org.pancakelab.model.pancakes.Pancake;

import java.util.List;
import java.util.UUID;

public interface IPancakeService {
    Order createOrder(int building, int room);
    
    void addPancake(UUID orderId, Pancake pancake);
    
    List<Pancake> viewOrder(UUID orderId);
    
    void removePancake(UUID orderId, UUID pancakeId);
    
    void cancelOrder(UUID orderId);
    
    void completeOrder(UUID orderId);
    
    List<Order> listCompletedOrders();
    
    void prepareOrder(UUID orderId);
    
    List<Order> listPreparedOrders();
    
    DeliveryInfo deliverOrder(UUID orderId);
} 
