package restaurant.filters;

import java.util.HashMap;
import java.util.Map;
import restaurant.controllers.*;
import restaurant.controllers.admin.*;
import restaurant.controllers.client.*;

public class ControllerMappings {

    private static final Map<String, IController> controllersByURL = new HashMap<String, IController>();

    static {
        controllersByURL.put("/app/admin/menu", new restaurant.controllers.admin.MenuController());
        controllersByURL.put("/app/admin/menu/create", new CreateMenuItemController());
        controllersByURL.put("/app/admin/orders", new ViewOrdersByClientController());
        controllersByURL.put("/app/admin/orders/confirm", new ConfirmOrderController());
        controllersByURL.put("/app/admin/clients", new ViewClientsController());
        controllersByURL.put("/app/admin/clients/create", new CreateClientController());

        controllersByURL.put("/app/client/menu", new restaurant.controllers.client.MenuController());
        controllersByURL.put("/app/client/orders", new ViewOrdersController());
        controllersByURL.put("/app/client/order/create", new CreateOrderController());
        controllersByURL.put("/app/client/order/pay", new PayOrderController());

        controllersByURL.put("/app/login", new LoginController());
        controllersByURL.put("/app/home", new HomeController());
    }

    public static IController resolveControllerForRequest(String path) {
        return controllersByURL.get(path);
    }
}
