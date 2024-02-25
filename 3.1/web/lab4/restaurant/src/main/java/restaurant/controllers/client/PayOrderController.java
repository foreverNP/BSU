package restaurant.controllers.client;

import restaurant.services.OrderService;
import restaurant.controllers.IController;
import restaurant.entity.Order;
import restaurant.exceptions.OrderServiceException;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.IWebSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.Writer;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class PayOrderController implements IController {

    private static final Logger logger = LogManager.getLogger(PayOrderController.class);

    private final OrderService orderService;

    public PayOrderController() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurantPU");
        this.orderService = new OrderService(emf);
    }

    @Override
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer)
            throws Exception {
        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        IWebRequest request = webExchange.getRequest();
        IWebSession session = webExchange.getSession();

        if (session == null || session.getAttributeValue("clientId") == null) {
            ctx.setVariable("errorMessage", "You are not authorized.");
            templateEngine.process("error", ctx, writer);
            return;
        }

        int clientId = (Integer) session.getAttributeValue("clientId");

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String orderIdStr = request.getParameterValue("orderId");

            try {
                int orderId = Integer.parseInt(orderIdStr);
                Order order = orderService.getOrderById(orderId);

                if (order != null && order.getClient().getId() == clientId) {
                    boolean success = orderService.pay(order, clientId);

                    if (success) {
                        ctx.setVariable("message", "Order successfully paid.");
                    } else {
                        ctx.setVariable("errorMessage", "Failed to pay for order.");
                    }
                } else {
                    ctx.setVariable("errorMessage", "Order not found or belongs to another client.");
                }
            } catch (NumberFormatException e) {
                logger.error("Invalid order ID format", e);
                ctx.setVariable("errorMessage", "Invalid order ID format.");
            } catch (OrderServiceException e) {
                logger.error("Failed to pay for order", e);
                ctx.setVariable("errorMessage", "Error paying for order: " + e.getMessage());
            }
        }

        templateEngine.process("client/pay_order", ctx, writer);
    }
}