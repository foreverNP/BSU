package restaurant.controllers.client;

import restaurant.services.OrderService;
import restaurant.controllers.IController;
import restaurant.entity.Order;
import restaurant.exceptions.OrderServiceException;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.Writer;

import java.util.List;

public class ViewOrdersController implements IController {

    private static final Logger logger = LogManager.getLogger(ViewOrdersController.class);

    private final OrderService orderService;

    public ViewOrdersController() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurantPU");
        this.orderService = new OrderService(emf);
    }

    @Override
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer)
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
            List<Order> orders = orderService.getOrdersByClientId(clientId);
            ctx.setVariable("orders", orders);
            templateEngine.process("client/view_orders", ctx, writer);
        } catch (OrderServiceException e) {
            logger.error("Failed to retrieve orders", e);
            ctx.setVariable("errorMessage", "Error retrieving orders.");
            templateEngine.process("error", ctx, writer);
        }
    }
}
