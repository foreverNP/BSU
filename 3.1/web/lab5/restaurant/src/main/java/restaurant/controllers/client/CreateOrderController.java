package restaurant.controllers.client;

import restaurant.services.OrderService;
import restaurant.services.MenuService;
import restaurant.controllers.IController;
import restaurant.entity.Menu;
import restaurant.entity.MenuItem;
import restaurant.entity.OrderItem;
import restaurant.exceptions.OrderServiceException;
import restaurant.exceptions.MenuServiceException;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.io.Writer;

public class CreateOrderController implements IController {

    private static final Logger logger = LogManager.getLogger(CreateOrderController.class);

    private final OrderService orderService;
    private final MenuService menuService;

    public CreateOrderController() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurantPU");
        this.orderService = new OrderService(emf);
        this.menuService = new MenuService(emf);
    }

    @Override
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer,
            final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        IWebSession session = webExchange.getSession();

        if (session == null || session.getAttributeValue("clientId") == null) {
            ctx.setVariable("errorMessage", "Not authorized.");
            templateEngine.process("error", ctx, writer);
            return;
        }

        int clientId = (Integer) session.getAttributeValue("clientId");

        try {
            Menu menu = menuService.getMenu();
            ctx.setVariable("menu", menu);

            if ("POST".equalsIgnoreCase(request.getMethod())) {
                String[] itemIds = request.getParameterValues("itemIds");

                if (itemIds != null && itemIds.length > 0) {
                    List<OrderItem> orderItems = new ArrayList<>();

                    for (String itemIdStr : itemIds) {
                        int itemId = Integer.parseInt(itemIdStr);
                        MenuItem menuItem = menuService.getMenuItemById(itemId);

                        if (menuItem != null) {
                            OrderItem orderItem = new OrderItem();
                            orderItem.setMenuItem(menuItem);
                            orderItem.setQuantity(1);
                            orderItems.add(orderItem);
                        }
                    }

                    boolean success = orderService.createOrder(clientId, orderItems);

                    if (success) {
                        ctx.setVariable("message", "Order successfully created.");
                    } else {
                        ctx.setVariable("errorMessage", "Cannot create order.");
                    }
                } else {
                    ctx.setVariable("errorMessage", "No items selected.");
                }
            }

            templateEngine.process("client/create_order", ctx, writer);
        } catch (MenuServiceException | OrderServiceException e) {
            logger.error("Failed to create order", e);
            ctx.setVariable("errorMessage", "Error creating order.");
            templateEngine.process("error", ctx, writer);
        }
    }
}
