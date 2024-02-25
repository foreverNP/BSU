import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;

import order.*;
import menu.*;

public class FoodDeliveryRemoteServer extends UnicastRemoteObject implements FoodDelivery.FoodDeliveryRemote {
    class Account {
        String name;
        long ID;
        List<Order> orders = new ArrayList<Order>();

        Account(String name, long ID) {
            this.name = name;
            this.ID = ID;
        }
    }

    public static final String DEFAULT_URL = "FoodDelivery";
    public static final int DEFAULT_PORT = 8080;
    private static final long serialVersionUID = -5298657303149865024L;
    private static final int MAX_COUNT_OF_USERS = 100;
    private static long IDcounter = 0;

    Map<Long, Account> users = new HashMap<Long, Account>();

    private Menu menu;

    protected FoodDeliveryRemoteServer() throws RemoteException {
        initMenu();
    }

    public synchronized long connect(String name) throws RemoteException, FoodDelivery.DeliveryException {
        if (users.size() == MAX_COUNT_OF_USERS) {
            throw new FoodDelivery.DeliveryException("507\nServer is already overloaded, wait for the opportunity to connect.");
        }

        users.put(IDcounter, new Account(name, IDcounter));
        System.out.println(name + " is connected as ID: " + IDcounter);

        return IDcounter++;
    }

    public synchronized void disconnect(long ID) throws RemoteException, FoodDelivery.DeliveryException {
        if (!users.containsKey(ID)) {
            throw new FoodDelivery.DeliveryException("404\nThe user was not found");
        }

        Account account = users.remove(ID);

        System.out.println(account.name + " registered as " + (account.ID) + " is disconnected");
    }

    public Menu getMenu() throws RemoteException, FoodDelivery.DeliveryException {
        if (menu == null) {
            throw new FoodDelivery.DeliveryException("503\nThe menu is not available");
        }

        return menu;
    }

    public synchronized void putOrder(long ID, Order order) throws RemoteException, FoodDelivery.DeliveryException {
        if (!users.containsKey(ID)) {
            throw new FoodDelivery.DeliveryException("404\nThe user was not found");
        }

        users.get(ID).orders.add(order);
    }

    public List<Order> getOrders(long ID) throws RemoteException, FoodDelivery.DeliveryException {
        if (!users.containsKey(ID)) {
            throw new FoodDelivery.DeliveryException("404\nThe user was not found");
        }

        return new ArrayList<>(users.get(ID).orders);
    }

    private void initMenu() {
        Dish pizza = new Dish(10.99, "Pizza", "Delicious pizza with cheese and tomatoes", Dish.Type.MAIN_COURSE);
        Dish burger = new Dish(8.5, "Burger", "Juicy beef burger with lettuce and tomato", Dish.Type.MAIN_COURSE);
        Dish cake = new Dish(5.99, "Cake", "Chocolate cake with frosting", Dish.Type.DESSERT);
        Dish iceCream = new Dish(3.5, "Ice Cream", "Vanilla ice cream with chocolate syrup", Dish.Type.DESSERT);
        Dish cola = new Dish(2.5, "Cola", "Carbonated beverage", Dish.Type.DRINK);
        Dish tea = new Dish(1.99, "Tea", "Hot tea with lemon", Dish.Type.DRINK);

        menu = new Menu();

        menu.addDish(pizza);
        menu.addDish(burger);
        menu.addDish(cake);
        menu.addDish(iceCream);
        menu.addDish(cola);
        menu.addDish(tea);
    }

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", Inet4Address.getLocalHost().getHostAddress());

            FoodDeliveryRemoteServer game = new FoodDeliveryRemoteServer();
            Registry registry = LocateRegistry.createRegistry(DEFAULT_PORT);
            registry.rebind(DEFAULT_URL, game);

            System.out.println(DEFAULT_URL + " is open and ready for customers");
            System.out.println("Server IP:" + Inet4Address.getLocalHost().getHostAddress());
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}