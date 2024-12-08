package restaurant.controllers.admin;

import java.io.Writer;

import restaurant.services.OrderService;
import restaurant.controllers.IController;
import restaurant.exceptions.OrderServiceException;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ConfirmOrderController implements IController {

    private static final Logger logger = LogManager.getLogger(ConfirmOrderController.class);

    private final OrderService orderService;

    public ConfirmOrderController() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurantPU");
        this.orderService = new OrderService(emf);
    }

    @Override
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer,
            final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String orderIdStr = request.getParameter("orderId");

            try {
                int orderId = Integer.parseInt(orderIdStr);
                boolean success = orderService.confirmByAdmin(orderId);

                if (success) {
                    ctx.setVariable("message", "Order successfully confirmed.");
                } else {
                    ctx.setVariable("errorMessage", "Failed to confirm order.");
                }
            } catch (NumberFormatException e) {
                logger.error("Invalid order ID format", e);
                ctx.setVariable("errorMessage", "Invalid order ID format.");
            } catch (OrderServiceException e) {
                logger.error("Failed to confirm order", e);
                ctx.setVariable("errorMessage", "Error confirming order.");
            }
        }

        templateEngine.process("admin/confirm_order", ctx, writer);
    }
}