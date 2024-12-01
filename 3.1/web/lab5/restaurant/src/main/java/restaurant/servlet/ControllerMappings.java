package restaurant.servlet;

import java.util.HashMap;
import java.util.Map;
import restaurant.controllers.*;
import restaurant.controllers.admin.*;
import restaurant.controllers.client.*;

public class ControllerMappings {

    private static final Map<String, IController> controllersByURL = new HashMap<String, IController>();

    static {
        controllersByURL.put("/admin/menu", new restaurant.controllers.admin.MenuController());
        controllersByURL.put("/admin/orders", new ViewOrdersByClientController());
        controllersByURL.put("/admin/orders/confirm", new ConfirmOrderController());
        controllersByURL.put("/admin/clients", new ViewClientsController());
        controllersByURL.put("/admin/clients/create", new CreateClientController());

        controllersByURL.put("/client/menu", new restaurant.controllers.client.MenuController());
        controllersByURL.put("/client/orders", new ViewOrdersController());
        controllersByURL.put("/client/order/create", new CreateOrderController());
        controllersByURL.put("/client/order/pay", new PayOrderController());
        controllersByURL.put("/client/login", new LoginController());
    }

    public static IController resolveControllerForRequest(String path) {
        return controllersByURL.get(path);
    }
}
