import order.Order;
import menu.Menu;

import java.rmi.*;
import java.rmi.registry.*;
import java.util.*;

public class FoodDelivery {

    public interface FoodDeliveryRemote extends Remote {
        public static final String DEFAULT_URL = "FoodDelivery";
        public static final int DEFAULT_PORT = 8080;

        public long connect(String name) throws RemoteException, DeliveryException;

        public void disconnect(long ID) throws RemoteException, DeliveryException;

        public Menu getMenu() throws RemoteException, FoodDelivery.DeliveryException;

        public void putOrder(long ID, Order order) throws RemoteException, FoodDelivery.DeliveryException;

        public List<Order> getOrders(long ID) throws RemoteException, FoodDelivery.DeliveryException;
    }

    public static class DeliveryException extends Exception {
        private static final long serialVersionUID = 1L;

        public DeliveryException(String msg) {
            super(msg);
        }
    }

    private static Order makeOrder() {
        if (Client.menu == null) {
            System.out.println("Oops! It seems like you don't know our menu!");
            return null;
        } else {
            System.out.print("Put on your address here: ");
            String address = Client.in.nextLine();

            Order currentOrder = new Order(address);
            System.out.println("To continue order enter position data, to finish it - \"finish\".");
            int id, count;
            String buffer;
            while (true) {
                System.out.print("Please, enter dish's ID to order: ");
                buffer = Client.in.nextLine();
                if (buffer.equals("finish")) {
                    break;
                }
                id = Integer.parseInt(buffer.trim());
                System.out.print("Please, enter dish's count to order: ");
                buffer = Client.in.nextLine();
                if (buffer.equals("finish")) {
                    break;
                }
                count = Integer.parseInt(buffer.trim());
                if (id < 1 || id > 6 || count < 0) {
                    System.out.println("Oops! Something wrong, please, try this position again. ");
                } else {
                    currentOrder.addDish(Client.menu.getDishByNumberInMenu(id), count);
                    System.out.println(currentOrder.toString());
                }
            }
            return currentOrder;
        }
    }

    public static class Client {

        static Scanner in = new Scanner(System.in);
        static Menu menu = null;
        static FoodDeliveryRemote foodDelivery;
        static long id;

        public static void main(String[] args) throws RemoteException, DeliveryException {
            try {
                System.out.println("Enter the server IP-address:");
                String ip = in.nextLine();

                Registry registry = LocateRegistry.getRegistry(ip, FoodDeliveryRemote.DEFAULT_PORT);

                foodDelivery = (FoodDeliveryRemote) registry.lookup(FoodDeliveryRemote.DEFAULT_URL);

                System.out.println("Enter your name:");
                String name = in.nextLine();

                id = foodDelivery.connect(name);

                System.out.println("Hi, " + name);
                System.out.println("(m)enu/(o)rder/(g)et_orders/(h)elp/(c)lose");

                while (true) {
                    String cmd = in.nextLine();
                    switch (cmd) {
                        case "m":
                        case "menu":
                            Menu currentMenu = foodDelivery.getMenu();
                            Client.menu = currentMenu;
                            System.out.println(Client.menu.toString());
                            break;
                        case "o":
                        case "order":
                            foodDelivery.putOrder(id, makeOrder());
                            break;
                        case "g":
                        case "get_orders":
                            System.out.println("Your orders: \n");
                            int counter = 0;
                            for (Order object : foodDelivery.getOrders(id)) {
                                if (object != null) {
                                    ++counter;
                                    System.out.println("\n" + object.toString() + "\n");
                                }
                            }
                            if (counter == 0) {
                                System.out.println("No orders yet, make it now! \n");
                            }
                            break;
                        case "h":
                        case "help":
                            System.out.println("(m)enu/(o)rder/(g)et_orders/(h)elp/(c)lose");
                            break;
                        case "c":
                        case "close":
                            foodDelivery.disconnect(id);
                            System.out.println("Bye!");
                            return;
                        default:
                            System.out.println("Invalid command, try again");
                    }
                }
            } catch (RemoteException e) {
                foodDelivery.disconnect(id);
                System.err.println(e);
                return;
            } catch (DeliveryException e) {
                foodDelivery.disconnect(id);
                System.err.println(e.getMessage());
                return;
            } catch (Exception e) {
                foodDelivery.disconnect(id);
                System.err.println(e);
                return;
            }
        }
    }
}