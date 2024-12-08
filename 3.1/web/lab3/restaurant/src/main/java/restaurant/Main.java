package restaurant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import restaurant.services.MenuService;

import restaurant.cli.AdminCLI;
import restaurant.cli.ClientCLI;
import restaurant.models.MenuItem;

public class Main {
        private static final Logger logger = LogManager.getLogger(Main.class);

        public static void main(String[] args) {
                logger.info("Starting the application...");

                EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("RestaurantUnit");

                // добавить элементы в меню, если они отсутствуют
                try {
                        MenuService menuService = new MenuService(entityManagerFactory);

                        menuService.addMenuItem("Burger", 8.50, "main course");
                        menuService.addMenuItem("Salad", 5.00, "main course");
                        menuService.addMenuItem("Ice Cream", 3.50, "dessert");
                        menuService.addMenuItem("Coffee", 2.50, "drink");

                        logger.info("Menu items successfully inserted.");
                } catch (Exception e) {
                        logger.error("Failed to insert menu items", e);
                }

                try {
                        if (args.length > 0) {
                                String userType = args[0].toLowerCase();

                                switch (userType) {
                                        case "admin":
                                                AdminCLI adminCLI = new AdminCLI(entityManagerFactory);
                                                adminCLI.start();
                                                break;
                                        case "client":
                                                if (args.length > 1) {
                                                        int clientId = Integer.parseInt(args[1]);
                                                        ClientCLI clientCLI = new ClientCLI(clientId,
                                                                        entityManagerFactory);
                                                        clientCLI.start();
                                                } else {
                                                        System.out.println("Please provide a client ID.");
                                                }
                                                break;
                                        default:
                                                System.out.println(
                                                                "Invalid argument. Use 'admin' or 'client [clientId]'.");
                                }
                        } else {
                                System.out.println("Please specify 'admin' or 'client [clientId]' as an argument.");
                        }
                } catch (Exception e) {
                        logger.error("An unexpected error occurred in Main", e);
                } finally {
                        entityManagerFactory.close();
                        logger.info("Application terminated.");
                }
        }
}
