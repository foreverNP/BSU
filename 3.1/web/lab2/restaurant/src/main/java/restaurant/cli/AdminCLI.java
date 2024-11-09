package restaurant.cli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import restaurant.exceptions.ClientServiceException;
import restaurant.exceptions.MenuServiceException;
import restaurant.exceptions.OrderServiceException;
import restaurant.managers.MenuManager;
import restaurant.models.Client;
import restaurant.models.Menu.MenuItem;
import restaurant.models.Order;
import restaurant.services.ClientService;
import restaurant.services.MenuService;
import restaurant.services.OrderService;

import java.util.List;
import java.util.Scanner;

public class AdminCLI {

    private static final Logger logger = LogManager.getLogger(AdminCLI.class);

    private final OrderService orderService;
    private final MenuService menuService;
    private final ClientService clientService;
    private Scanner scanner;

    public AdminCLI() {
        this.orderService = OrderService.getInstance();
        this.menuService = MenuService.getInstance();
        this.clientService = ClientService.getInstance();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            try {
                System.out.println("1. View menu");
                System.out.println("2. Find a menu item");
                System.out.println("3. View all orders by client");
                System.out.println("4. Confirm an order");
                System.out.println("5. View all clients");
                System.out.println("6. Create a new client");
                System.out.println("7. Exit");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Очистка буфера

                switch (choice) {
                    case 1:
                        logger.debug("Admin called view menu endpoint");
                        viewMenu();
                        break;
                    case 2:
                        logger.debug("Admin called find menu item endpoint");
                        findMenuItem();
                        break;
                    case 3:
                        logger.debug("Admin called view all orders by client endpoint");
                        viewOrdersByClient();
                        break;
                    case 4:
                        logger.debug("Admin called confirm order endpoint");
                        confirmOrder();
                        break;
                    case 5:
                        logger.debug("Admin called view all clients endpoint");
                        viewAllClients();
                        break;
                    case 6:
                        logger.debug("Admin called create new client endpoint");
                        createClient();
                        break;
                    case 7:
                        logger.debug("Admin chose to exit");
                        return;
                    default:
                        System.out.println("Invalid option. Try again.");
                        logger.warn("Invalid option selected: {}", choice);
                }
            } catch (Exception e) {
                logger.error("Unexpected error in AdminCLI", e);
                System.out.println("An unexpected error occurred. Please try again.");
            }
        }
    }

    private void viewOrdersByClient() {
        try {
            System.out.println("Enter client ID:");
            int clientId = scanner.nextInt();
            scanner.nextLine();
            logger.debug("Client ID entered: {}", clientId);

            List<Order> orders = orderService.getOrdersByClientId(clientId);
            System.out.println(orders);
            logger.info("Successfully retrieved orders for client ID: {}", clientId);
        } catch (OrderServiceException e) {
            logger.error("Error retrieving orders for client ID", e);
            System.out.println("Failed to retrieve orders. Please try again.");
        } catch (Exception e) {
            logger.error("Unexpected error in viewOrdersByClient", e);
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

    private void viewAllClients() {
        try {
            List<Client> clients = clientService.getAllClients();
            System.out.println(clients);
            logger.info("Successfully retrieved all clients");
        } catch (ClientServiceException e) {
            logger.error("Error retrieving clients", e);
            System.out.println("Failed to retrieve clients. Please try again.");
        } catch (Exception e) {
            logger.error("Unexpected error in viewAllClients", e);
            System.out.println("An unexpected error occurred. Please try again.");
        }
    }

    private void createClient() {
        try {
            System.out.println("Enter client name:");
            String name = scanner.nextLine();
            logger.debug("Client name entered: {}", name);
            System.out.println("Enter client balance:");
            double balance = scanner.nextDouble();
            scanner.nextLine();
            logger.debug("Client balance entered: {}", balance);

            clientService.addClient(name, balance);
            logger.info("Successfully created client: {} with balance: {}", name, balance);
            System.out.println("Client created successfully.");
        } catch (ClientServiceException e) {
            logger.error("Error creating client", e);
            System.out.println("Failed to create client. Please try again.");
        } catch (Exception e) {
            logger.error("Unexpected error in createClient", e);
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

    private void confirmOrder() {
        try {
            System.out.println("Enter order ID to confirm:");
            int orderId = scanner.nextInt();
            scanner.nextLine();
            logger.debug("Order ID entered: {}", orderId);

            boolean confirmed = orderService.confirmByAdmin(orderId);
            if (confirmed) {
                logger.info("Successfully confirmed order ID: {}", orderId);
                System.out.println("Order confirmed successfully.");
            } else {
                logger.warn("Failed to confirm order ID: {}", orderId);
                System.out.println("Failed to confirm order.");
            }
        } catch (OrderServiceException e) {
            logger.error("Error confirming order", e);
            System.out.println("Failed to confirm order. Please try again.");
        } catch (Exception e) {
            logger.error("Unexpected error in confirmOrder", e);
            System.out.println("An unexpected error occurred. Please try again.");
        }
    }
}
