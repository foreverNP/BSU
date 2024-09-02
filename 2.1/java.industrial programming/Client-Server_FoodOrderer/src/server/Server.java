package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

import protocol.*;
import message.*;
import menu.*;
import order.*;

public class Server {

    private static int MAX_USERS = 100;

    private static Object syncUsers = new Object();
    private static TreeMap<String, ServerThread> users = new TreeMap<String, ServerThread>();
    private static Object syncFlags = new Object();
    private static boolean stopFlag = false;

    private static Menu menu = new Menu();
    private static List<Order> orders = new ArrayList<Order>();

    public static void main(String[] args) {

        try (ServerSocket serv = new ServerSocket(Protocol.DEFAULT_PORT)) {
            System.err.println("initialized");

            Dish pizza = new Dish(10.99, "Pizza", "Delicious pizza with cheese and tomatoes", Dish.Type.MAIN_COURSE);
            Dish burger = new Dish(8.5, "Burger", "Juicy beef burger with lettuce and tomato", Dish.Type.MAIN_COURSE);
            Dish cake = new Dish(5.99, "Cake", "Chocolate cake with frosting", Dish.Type.DESSERT);
            Dish iceCream = new Dish(3.5, "Ice Cream", "Vanilla ice cream with chocolate syrup", Dish.Type.DESSERT);
            Dish cola = new Dish(2.5, "Cola", "Carbonated beverage", Dish.Type.DRINK);
            Dish tea = new Dish(1.99, "Tea", "Hot tea with lemon", Dish.Type.DRINK);

            menu.addDish(pizza);
            menu.addDish(burger);
            menu.addDish(cake);
            menu.addDish(iceCream);
            menu.addDish(cola);
            menu.addDish(tea);

            ServerStopAndOrdersThread admin = new ServerStopAndOrdersThread();
            admin.start();

            while (true) {
                Socket sock = accept(serv);
                if (sock != null) {
                    if (Server.getNumUsers() < Server.MAX_USERS) {
                        System.err.println(sock.getInetAddress().getHostName() + " connected");
                        ServerThread server = new ServerThread(sock);
                        server.start();
                    } else {
                        System.err.println(sock.getInetAddress().getHostName() + " connection rejected");
                        sock.close();
                    }
                }
                if (Server.getStopFlag()) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            stopAllUsers();
            System.err.println("stopped");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    public static Socket accept(ServerSocket serv) {
        assert (serv != null);
        try {
            serv.setSoTimeout(1000);
            Socket sock = serv.accept();
            return sock;
        } catch (SocketException e) {
        } catch (IOException e) {
        }
        return null;
    }

    private static void stopAllUsers() {
        String[] nic = getUsers();
        for (String user : nic) {
            ServerThread ut = getUser(user);
            if (ut != null) {
                ut.disconnect();
            }
        }
    }

    public static boolean getStopFlag() {
        synchronized (Server.syncFlags) {
            return stopFlag;
        }
    }

    public static void setStopFlag(boolean value) {
        synchronized (Server.syncFlags) {
            stopFlag = value;
        }
    }


    public static ServerThread getUser(String userNic) {
        synchronized (Server.syncUsers) {
            return Server.users.get(userNic);
        }
    }

    public static ServerThread registerUser(String userNic, ServerThread user) {
        synchronized (Server.syncUsers) {
            ServerThread old = Server.users.get(userNic);
            if (old == null) {
                Server.users.put(userNic, user);
            }
            return old;
        }
    }

    public static ServerThread setUser(String userNic, ServerThread user) {
        synchronized (Server.syncUsers) {
            ServerThread res = Server.users.put(userNic, user);
            if (user == null) {
                Server.users.remove(userNic);
            }
            return res;
        }
    }

    public static String[] getUsers() {
        synchronized (Server.syncUsers) {
            return Server.users.keySet().toArray(new String[0]);
        }
    }

    public static int getNumUsers() {
        synchronized (Server.syncUsers) {
            return Server.users.keySet().size();
        }
    }

    public static Menu getMenu() {
        synchronized (Server.syncUsers) {
            return menu;
        }
    }

    public static List<Order> getOrders() {
        synchronized (Server.syncUsers) {
            return new ArrayList<Order>(orders);
        }
    }

    public static void addOrder(Order order) {
        synchronized (Server.syncUsers) {
            if (order != null) {
                orders.add(order);
            }
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////

class ServerThread extends Thread {

    private Socket sock;
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private InetAddress addr;

    private String userNic = null;
    private String userFullName = null;

    private Object syncLetters = new Object();
    private Vector<String> letters = null;

    private boolean disconnected = false;

    public ServerThread(Socket s) throws IOException {
        sock = s;
        s.setSoTimeout(1000);
        os = new ObjectOutputStream(s.getOutputStream());
        is = new ObjectInputStream(s.getInputStream());
        addr = s.getInetAddress();
        this.setDaemon(true);
    }

    public void run() {
        try {
            while (true) {
                Message msg = null;
                try {
                    msg = (Message) is.readObject();
                } catch (IOException e) {
                } catch (ClassNotFoundException e) {
                }
                if (msg != null) switch (msg.getID()) {
                    case Protocol.REQUEST_CONNECT:
                        if (!connect((MessageConnect) msg))
                            return;
                        break;

                    case Protocol.REQUEST_DISCONNECT:
                        return;

                    case Protocol.REQUEST_SEND_ORDER:
                        order((MessageSendOrder) msg);
                        break;

                    case Protocol.REQUEST_GET_MENU:
                        menu((MessageGetMenu) msg);
                        break;
                }
            }
        } catch (IOException e) {
            System.err.print("Disconnect...");
        } finally {
            disconnect();
        }
    }

    boolean connect(MessageConnect msg) throws IOException {
        ServerThread old = register(msg.firstName, msg.firstName + " " + msg.secondName + " " + msg.lastName);
        if (old == null) {
            os.writeObject(new MessageConnectResult());

            return true;
        } else {
            os.writeObject(new MessageConnectResult("User " + old.userFullName + " already connected as " + userNic));

            return false;
        }
    }


    void menu(MessageGetMenu msg) throws IOException {

        Menu menu = Server.getMenu();
        if (menu != null)
            os.writeObject(new MessageGetMenuResult(menu));
        else
            os.writeObject(new MessageGetMenuResult("Unable to get menu"));
    }

    void order(MessageSendOrder msg) throws IOException {

        Order order = msg.getOrder();

        if (order == null) {
            os.writeObject(new MessageSendOrderResult("Unable to accept order"));
        } else {
            Server.addOrder(order);
            os.writeObject(new MessageSendOrderResult());
        }
    }

    public void disconnect() {
        if (!disconnected)
            try {
                System.err.println(addr.getHostName() + " disconnected");
                unregister();
                os.close();
                is.close();
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.interrupt();
                disconnected = true;
            }
    }

    private void unregister() {
        if (userNic != null) {
            Server.setUser(userNic, null);
            userNic = null;
        }
    }

    private ServerThread register(String nic, String name) {
        ServerThread old = Server.registerUser(nic, this);
        if (old == null) {
            if (userNic == null) {
                userNic = nic;
                userFullName = name;
                System.err.println("User \'" + name + "\' registered as \'" + nic + "\'");
            }
        }
        return old;
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////

class ServerStopAndOrdersThread extends CommandThread {
    static final String cmdStop = "q";
    static final String cmdLStop = "quit";

    static final String cmdOrders = "o";
    static final String cmdLOrders = "orders";

    Scanner fin;

    public ServerStopAndOrdersThread() {
        fin = new Scanner(System.in);

        Server.setStopFlag(false);

        putHandler(cmdStop, cmdLStop, new CmdHandler() {
            @Override
            public boolean onCommand(int[] errorCode) {
                return onCmdQuit();
            }
        });

        putHandler(cmdOrders, cmdLOrders, new CmdHandler() {
            @Override
            public boolean onCommand(int[] errorCode) {
                return onCmdOrders();
            }
        });

        this.setDaemon(true);

        System.err.println("Enter \'" + cmdStop + "\' or \'" + cmdLStop + "\' to stop server");
        System.err.println("Enter \'" + cmdOrders + "\' or \'" + cmdLOrders + "\' to print orders\n");
    }

    public void run() {

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
            if (fin.hasNextLine() == false)
                continue;
            String str = fin.nextLine();
            if (command(str)) {
                break;
            }
        }
    }

    public boolean onCmdQuit() {
        System.err.print("stop server...");
        fin.close();
        Server.setStopFlag(true);
        return true;
    }

    public boolean onCmdOrders() {
        List<Order> orders = Server.getOrders();

        if (orders.isEmpty()) {
            System.out.println("There is no orders now");
            return false;
        }

        for (Order o : orders) {
            System.out.println(o);
        }

        return false;
    }
}