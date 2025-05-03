package org.pancakelab.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String orderId) {
        super(String.format("Order with ID [%s] not found", orderId));
    }

    public OrderNotFoundException(String orderId, Throwable cause) {
        super(String.format("Order with ID [%s] not found", orderId), cause);
    }
}
