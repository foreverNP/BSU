package restaurant.controllers.admin;

import restaurant.services.OrderService;
import restaurant.controllers.IController;
import restaurant.entity.Order;
import restaurant.exceptions.OrderServiceException;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebRequest;
import java.io.Writer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public class ViewOrdersByClientController implements IController {

    private static final Logger logger = LogManager.getLogger(ViewOrdersByClientController.class);

    private final OrderService orderService;

    public ViewOrdersByClientController() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurantPU");
        this.orderService = new OrderService(emf);
    }

    @Override
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer,
            final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String clientIdStr = request.getParameter("clientId");

            try {
                int clientId = Integer.parseInt(clientIdStr);
                List<Order> orders = orderService.getOrdersByClientId(clientId);
                ctx.setVariable("orders", orders);
            } catch (NumberFormatException e) {
                logger.error("Invalid client ID format", e);
                ctx.setVariable("errorMessage", "Invalid client ID format.");
            } catch (OrderServiceException e) {
                logger.error("Failed to retrieve orders", e);
                ctx.setVariable("errorMessage", "Error retrieving orders.");
            }
        }

        templateEngine.process("admin/view_orders_by_client", ctx, writer);
    }
}
