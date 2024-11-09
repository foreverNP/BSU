package restaurant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import restaurant.cli.AdminCLI;
import restaurant.cli.ClientCLI;
import restaurant.db.JdbcMigrator;

public class Main {
        private static final Logger logger = LogManager.getLogger(Main.class);

        public static void main(String[] args) {
                logger.info("Starting the application...");

                JdbcMigrator.migrate();

                if (args.length > 0) {
                        String userType = args[0].toLowerCase();

                        switch (userType) {
                                case "admin":
                                        AdminCLI adminCLI = new AdminCLI();
                                        adminCLI.start();
                                        break;
                                case "client":
                                        if (args.length > 1) {
                                                int clientId = Integer.parseInt(args[1]);
                                                ClientCLI clientCLI = new ClientCLI(clientId);
                                                clientCLI.start();
                                        } else {
                                                System.out.println("Please provide a client ID.");
                                        }
                                        break;
                                default:
                                        System.out.println("Invalid argument. Use 'admin' or 'client [clientId]'.");
                        }
                } else {
                        System.out.println("Please specify 'admin' or 'client [clientId]' as an argument.");
                }
        }
}
