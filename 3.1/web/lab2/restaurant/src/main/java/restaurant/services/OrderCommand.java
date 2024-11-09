package restaurant.services;

import restaurant.exceptions.OrderServiceException;

public interface OrderCommand {
    boolean execute(int orderId) throws OrderServiceException;

    public class ChangeStatusOrderCommand implements OrderCommand {
        private OrderStatusStrategy strategy;

        public ChangeStatusOrderCommand(OrderStatusStrategy strategy) {
            this.strategy = strategy;
        }

        @Override
        public boolean execute(int orderId) throws OrderServiceException {
            return strategy.processOrder(orderId);
        }
    }
}