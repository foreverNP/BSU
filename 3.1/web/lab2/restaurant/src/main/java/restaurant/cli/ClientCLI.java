package restaurant.cli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import restaurant.services.OrderService;
import restaurant.services.MenuService;
import restaurant.exceptions.OrderServiceException;
import restaurant.exceptions.MenuServiceException;
import restaurant.models.Order;
import restaurant.dao.OrderDAO;
import restaurant.models.Bill;
import restaurant.models.Order.OrderItem;
import restaurant.models.Menu.MenuItem;
import restaurant.managers.MenuManager;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientCLI {

    private static final Logger logger = LogManager.getLogger(ClientCLI.class);

    private final int clientId;
    private final OrderService orderService;
    private final MenuService menuService;
    private Scanner scanner;

    public ClientCLI(int clientId) {
        this.clientId = clientId;
        this.orderService = OrderService.getInstance();
        this.menuService = MenuService.getInstance();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            try {
                System.out.println("1. View my orders");
                System.out.println("2. View menu");
                System.out.println("3. Find a menu item");
                System.out.println("4. Make an order");
                System.out.println("5. Pay for order");
                System.out.println("6. Exit");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Очистка буфера

                switch (choice) {
                    case 1:
                        logger.debug("Client called view orders endpoint");
                        viewOrders();
                        break;
                    case 2:
                        logger.debug("Client called view menu endpoint");
                        viewMenu();
                        break;
                    case 3:
                        logger.debug("Client called find menu item endpoint");
                        findMenuItem();
                        break;
                    case 4:
                        logger.debug("Client called make order endpoint");
                        makeOrder();
                        break;
                    case 5:
                        logger.debug("Client called pay order endpoint");
                        payOrder();
                        break;
                    case 6:
                        logger.debug("Client chose to exit");
                        return;
                    default:
                        System.out.println("Invalid option. Try again.");
                        logger.warn("Invalid option selected: {}", choice);
                }
            } catch (Exception e) {
                logger.error("Unexpected error in ClientCLI", e);
                System.out.println("An unexpected error occurred. Please try again.");
            }
        }
    }

    private void viewOrders() {
        try {
            List<Order> orders = orderService.getOrdersByClientId(clientId);
            System.out.println(orders);
            logger.info("Successfully retrieved orders for client ID: {}", clientId);
        } catch (OrderServiceException e) {
            logger.error("Error retrieving orders for client ID: {}", clientId, e);
            System.out.println("Failed to retrieve orders. Please try again.");
        } catch (Exception e) {
            logger.error("Unexpected error in viewOrders", e);
            System.out.println("An unexpected error occurred. Please try again.");
        }
    }

    private void viewMenu() {
        try {
            MenuManager menuManager = new MenuManager(menuService.getMenu());
            menuManager.sortMenuItemsByPrice();
            System.out.println(menuManager.getMenuAsString());
            logger.info("Successfully retrieved and sorted the menu");
        } catch (MenuServiceException e) {
            logger.error("Error retrieving menu", e);
            System.out.println("Failed to retrieve the menu. Please try again.");
        } catch (Exception e) {
            logger.error("Unexpected error in viewMenu", e);
            System.out.println("An unexpected error occurred. Please try again.");
        }
    }

    private void findMenuItem() {
        try {
            System.out.println("Enter menu item name:");
            String name = scanner.nextLine();
            logger.debug("Menu item name entered: {}", name);

            MenuManager menuManager = new MenuManager(menuService.getMenu());
            List<MenuItem> items = menuManager.searchMenuItemsByName(name);

            System.out.println(items);
            logger.info("Successfully searched for menu items with name: {}", name);
        } catch (MenuServiceException e) {
            logger.error("Error searching for menu item", e);
            System.out.println("Failed to search for menu items. Please try again.");
        } catch (Exception e) {
            logger.error("Unexpected error in findMenuItem", e);
            System.out.println("An unexpected error occurred. Please try again.");
        }
    }

    private void makeOrder() {
        try {
            viewMenu();
            List<OrderItem> orderItems = new ArrayList<>();
            boolean ordering = true;

            while (ordering) {
                System.out.println("Enter menu item ID to order:");
                int itemId = scanner.nextInt();
                System.out.println("Enter quantity:");
                int quantity = scanner.nextInt();
                logger.debug("Menu item ID entered: {}, quantity: {}", itemId, quantity);

                MenuItem menuItem = menuService.getMenuItemById(itemId);
                if (menuItem != null) {
                    OrderItem orderItem = new OrderItem(menuItem, quantity);
                    orderItems.add(orderItem);
                    System.out.println("Item added to order.");
                    logger.info("Added item {} to order", menuItem.getName());
                } else {
                    System.out.println("Invalid menu item.");
                    logger.warn("Invalid menu item ID: {}", itemId);
                }

                System.out.println("Do you want to add another item? (y/n)");
                String response = scanner.next();
                if (!response.equalsIgnoreCase("y")) {
                    ordering = false;
                }
            }

            if (!orderItems.isEmpty()) {
                orderService.createOrder(clientId, orderItems);
                System.out.println("Order placed successfully.");
                logger.info("Order placed for client ID: {}", clientId);
            } else {
                System.out.println("No items in order.");
                logger.warn("No items in order for client ID: {}", clientId);
            }
        } catch (OrderServiceException e) {
            logger.error("Error creating order for client ID: {}", clientId, e);
            System.out.println("Failed to create order. Please try again.");
        } catch (Exception e) {
            logger.error("Unexpected error in makeOrder", e);
            System.out.println("An unexpected error occurred. Please try again.");
        }
    }

    private void payOrder() {
        try {
            System.out.println("Enter order ID to pay:");
            int orderId = scanner.nextInt();
            scanner.nextLine(); // Очистка буфера
            logger.debug("Order ID entered: {}", orderId);

            Order order = orderService.getOrderById(orderId);

            if (order == null) {
                System.out.println("Invalid order.");
                logger.warn("Invalid order ID: {}", orderId);
                return;
            }

            if (order.getStatus().getId() == OrderDAO.STATUS_ID_COMPLETED) {
                System.out.println("Order is already paid.");
                logger.info("Order {} is already paid", orderId);
                return;
            }

            if (order.getStatus().getId() != OrderDAO.STATUS_ID_COOKED) {
                System.out.println("Order is not ready to pay.");
                logger.warn("Order {} is not ready to be paid", orderId);
                return;
            }

            Bill bill = new Bill(order);
            System.out.println(bill);
            logger.info("Generated bill for order ID: {}", orderId);

            if (orderService.pay(order, clientId)) {
                System.out.println("Order paid successfully.");
                logger.info("Order {} paid successfully by client ID: {}", orderId, clientId);
            } else {
                System.out.println("Not enough money on the balance.");
                logger.warn("Insufficient balance for client ID: {}", clientId);
            }
        } catch (OrderServiceException e) {
            logger.error("Error paying for order", e);
            System.out.println("Failed to pay for order. Please try again.");
        } catch (Exception e) {
            logger.error("Unexpected error in payOrder", e);
            System.out.println("An unexpected error occurred. Please try again.");
        }
    }
}
