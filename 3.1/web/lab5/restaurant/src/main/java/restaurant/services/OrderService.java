package restaurant.services;

import restaurant.dao.OrderDAO;
import restaurant.entity.Client;
import restaurant.entity.Order;
import restaurant.entity.OrderItem;
import restaurant.dao.ClientDAO;
import restaurant.exceptions.DAOException;
import restaurant.exceptions.OrderServiceException;
import restaurant.mock.KitchenMock;
import restaurant.mock.KitchenSubject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class OrderService {

    private static final Logger logger = LogManager.getLogger(OrderService.class);

    private final OrderDAO orderDAO;
    private final ClientDAO clientDAO;

    public OrderService(EntityManagerFactory emf) {
        this.orderDAO = new OrderDAO(emf);
        this.clientDAO = new ClientDAO(emf);
    }

    // Получить все заказы клиента
    public List<Order> getOrdersByClientId(int clientId) throws OrderServiceException {
        try {
            return orderDAO.getOrdersByClientId(clientId);
        } catch (Exception e) {
            throw new OrderServiceException("Failed to fetch orders for client ID: " + clientId, e);
        }
    }

    // Получить заказ по ID
    public Order getOrderById(int orderId) throws OrderServiceException {
        try {
            return orderDAO.getOrderById(orderId);
        } catch (Exception e) {
            throw new OrderServiceException("Failed to fetch order by ID: " + orderId, e);
        }
    }

    // Создать новый заказ
    public boolean createOrder(int clientId, List<OrderItem> items) throws OrderServiceException {
        try {

            Client client = clientDAO.getClientById(clientId);
            if (client == null) {
                throw new OrderServiceException("Client not found for ID: " + clientId);
            }

            boolean result = orderDAO.createOrder(client, items);
            return result;
        } catch (Exception e) {
            throw new OrderServiceException("Failed to create order for client ID: " + clientId, e);
        }
    }

    // Подтвердить заказ администратором и начать готовку
    public boolean confirmByAdmin(int orderId) throws OrderServiceException {
        try {
            Order order = getOrderById(orderId);
            if (order == null) {
                logger.warn("Order not found for ID: {}", orderId);
                return false;
            }
            if (!Order.pendingStatus.equals(order.getStatus())) {
                logger.warn("Order not pending for ID: {}", orderId + ". Status: " + order.getStatus());
                return false;
            }

            order.setStatus(Order.cookingStatus);
            orderDAO.updateOrderStatus(orderId, Order.cookingStatus);

            KitchenSubject kitchenSubject = new KitchenSubject();
            kitchenSubject.addObserver(new KitchenSubject.KitchenObserver() {
                @Override
                public void onOrderPrepared(int orderId) {
                    try {
                        orderDAO.updateOrderStatus(orderId, Order.cookedStatus);
                        System.out.println("Order " + orderId + " is cooked");
                    } catch (DAOException e) {
                        logger.error("Error marking order as cooked for ID: {}", orderId, e);
                    }
                }
            });

            Thread kitchenThread = new Thread(new KitchenMock(orderId, kitchenSubject));
            kitchenThread.start();

            return true;
        } catch (Exception e) {
            throw new OrderServiceException("Failed to confirm order by admin for ID: " + orderId, e);
        }
    }

    // Отметить, что заказ готов к выдаче
    public boolean orderReady(int orderId) throws OrderServiceException {
        try {
            return orderDAO.updateOrderStatus(orderId, Order.cookedStatus);
        } catch (Exception e) {
            throw new OrderServiceException("Failed to mark order as ready for ID: " + orderId, e);
        }
    }

    // Завершить заказ
    public boolean completeOrder(int orderId) throws OrderServiceException {
        try {
            return orderDAO.updateOrderStatus(orderId, Order.completedStatus);
        } catch (Exception e) {
            throw new OrderServiceException("Failed to complete order for ID: " + orderId, e);
        }
    }

    // Оплатить заказ
    public boolean pay(Order order, int clientId) throws OrderServiceException {
        try {
            Client client = clientDAO.getClientById(clientId);
            if (client == null) {
                throw new OrderServiceException("Client not found for ID: " + clientId);
            }

            if (client.getBalance().compareTo(order.getTotalPrice()) < 0) {
                throw new OrderServiceException("Not enough money on the balance for client ID: " + clientId);
            }

            if (!order.getStatus().equals(Order.cookedStatus)) {
                throw new OrderServiceException("Order status is: " + order.getStatus() + ". You can't pay for it.");
            }

            clientDAO.updateClientBalance(clientId,
                    client.getBalance().subtract(order.getTotalPrice()).doubleValue());
            boolean result = completeOrder(order.getId());

            return result;
        } catch (Exception e) {
            throw new OrderServiceException(
                    "Failed to process payment for order ID: " + order.getId() + " by client ID: " + clientId, e);
        }
    }
}
