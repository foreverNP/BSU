package restaurant.cli;

import jakarta.persistence.EntityManagerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import restaurant.services.OrderService;
import restaurant.services.MenuService;
import restaurant.exceptions.OrderServiceException;
import restaurant.exceptions.MenuServiceException;
import restaurant.models.Order;

import restaurant.models.OrderItem;
import restaurant.models.MenuItem;
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

    public ClientCLI(int clientId, EntityManagerFactory emf) {
        this.clientId = clientId;
        this.orderService = new OrderService(emf);
        this.menuService = new MenuService(emf);
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
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        viewOrders();
                        break;
                    case 2:
                        viewMenu();
                        break;
                    case 3:
                        findMenuItem();
                        break;
                    case 4:
                        makeOrder();
                        break;
                    case 5:
                        payOrder();
                        break;
                    case 6:
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
        logger.debug("Client called viewOrders endpoint");
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
        logger.debug("Client called viewMenu endpoint");
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
        logger.debug("Client called findMenuItem endpoint");
        try {
            System.out.println("Enter menu item name:");
            String name = scanner.nextLine();

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
        logger.debug("Client called makeOrder endpoint");
        try {
            viewMenu();
            List<OrderItem> orderItems = new ArrayList<>();
            boolean ordering = true;

            while (ordering) {
                System.out.println("Enter menu item ID to order:");
                int itemId = scanner.nextInt();
                System.out.println("Enter quantity:");
                int quantity = scanner.nextInt();

                MenuItem menuItem = menuService.getMenuItemById(itemId);
                if (menuItem != null) {
                    OrderItem orderItem = new OrderItem(null, menuItem, quantity);
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
        logger.debug("Client called payOrder endpoint");
        try {
            System.out.println("Enter order ID to pay:");
            int orderId = scanner.nextInt();
            scanner.nextLine();

            Order order = orderService.getOrderById(orderId);

            if (orderService.pay(order, clientId)) {
                System.out.println("Order paid successfully.");
                logger.info("Order {} paid successfully", orderId);
            } else {
                System.out.println("Failed to pay for order.");
                logger.warn("Failed to pay for order: {}", orderId);
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