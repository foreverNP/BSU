package restaurant.services;

import restaurant.dao.OrderDAO;
import restaurant.exceptions.OrderServiceException;
import restaurant.models.Order;
import restaurant.models.Client;
import restaurant.models.Order.OrderItem;
import restaurant.mock.KitchenMock;
import restaurant.mock.KitchenSubject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class OrderService {

    private static final Logger logger = LogManager.getLogger(OrderService.class);

    private static OrderService instance;
    private final OrderDAO orderDAO;

    private OrderService() {
        this.orderDAO = new OrderDAO();
    }

    public static synchronized OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
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
            return orderDAO.createOrder(clientId, items);
        } catch (Exception e) {
            throw new OrderServiceException("Failed to create order for client ID: " + clientId, e);
        }
    }

    // Подтвердить заказ (заказ начинает готовиться)
    public boolean confirmOrder(int orderId) throws OrderServiceException {
        try {
            return orderDAO.confirmOrder(orderId);
        } catch (Exception e) {
            throw new OrderServiceException("Failed to confirm order ID: " + orderId, e);
        }
    }

    // Подтвердить заказ администратором
    public boolean confirmByAdmin(int orderId) throws OrderServiceException {
        try {
            Order order = getOrderById(orderId);

            if (order == null) {
                logger.debug("Order not found for ID: {}", orderId);
                return false;
            }

            if (order.getStatus().getId() != OrderDAO.STATUS_ID_PENDING) {
                logger.debug("Order is not pending for ID: {}", orderId);
                return false;
            }

            OrderStatusStrategy confirmStrategy = new OrderStatusStrategy.ConfirmOrderStrategy();
            OrderCommand confirm = new OrderCommand.ChangeStatusOrderCommand(confirmStrategy);

            if (!confirm.execute(orderId)) {
                throw new OrderServiceException("Failed to confirm order by admin for ID: " + orderId);
            }
            System.out.println("Order " + orderId + " is confirmed");

            OrderStatusStrategy readyStrategy = new OrderStatusStrategy.ReadyOrderStrategy();
            OrderCommand ready = new OrderCommand.ChangeStatusOrderCommand(readyStrategy);

            KitchenSubject kitchenSubject = new KitchenSubject();
            kitchenSubject.addObserver(new KitchenSubject.KitchenObserver() {
                @Override
                public void onOrderPrepared(int orderId) {
                    try {
                        if (ready.execute(orderId)) {
                            System.out.println("Order " + orderId + " is cooked");
                        } else {
                            System.out.println("Order " + orderId + " is not cooked");
                        }
                    } catch (OrderServiceException e) {
                        logger.debug("Error marking order as ready for ID: {}", orderId, e);
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
            return orderDAO.orderReady(orderId);
        } catch (Exception e) {
            throw new OrderServiceException("Failed to mark order as ready for ID: " + orderId, e);
        }
    }

    // Завершить заказ
    public boolean completeOrder(int orderId) throws OrderServiceException {
        try {
            return orderDAO.completeOrder(orderId);
        } catch (Exception e) {
            throw new OrderServiceException("Failed to complete order for ID: " + orderId, e);
        }
    }

    // Оплатить заказ
    public boolean pay(Order order, int clientId) throws OrderServiceException {
        try {
            Client client = ClientService.getInstance().getClientById(clientId);

            if (client == null) {
                throw new OrderServiceException("Client not found for ID: " + clientId);
            }

            if (client.getBalance().compareTo(order.getTotalPrice()) < 0) {
                throw new OrderServiceException("Not enough money on the balance for client ID: " + clientId);
            }

            ClientService.getInstance().updateClientBalance(clientId,
                    client.getBalance().subtract(order.getTotalPrice()).doubleValue());
            return completeOrder(order.getId());
        } catch (Exception e) {
            throw new OrderServiceException(
                    "Failed to process payment for order ID: " + order.getId() + " by client ID: " + clientId, e);
        }
    }
}
