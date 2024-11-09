package restaurant.services;

import restaurant.exceptions.OrderServiceException;

public interface OrderStatusStrategy {
    boolean processOrder(int orderId) throws OrderServiceException;

    class ConfirmOrderStrategy implements OrderStatusStrategy {
        @Override
        public boolean processOrder(int orderId) throws OrderServiceException {
            return OrderService.getInstance().confirmOrder(orderId);
        }
    }

    class ReadyOrderStrategy implements OrderStatusStrategy {
        @Override
        public boolean processOrder(int orderId) throws OrderServiceException {
            return OrderService.getInstance().orderReady(orderId);
        }
    }

    class CompleteOrderStrategy implements OrderStatusStrategy {
        @Override
        public boolean processOrder(int orderId) throws OrderServiceException {
            return OrderService.getInstance().completeOrder(orderId);
        }
    }
}