package org.pancakelab.service;

import org.pancakelab.model.Order;

import java.util.List;

public class DeliveryInfo {
    private final Order order;
    private final List<String> pancakeDescriptions;

    public DeliveryInfo(Order order, List<String> pancakeDescriptions) {
        this.order = order;
        this.pancakeDescriptions = pancakeDescriptions;
    }

    public Order getOrder() {
        return order;
    }

    public List<String> getPancakeDescriptions() {
        return pancakeDescriptions;
    }
}
