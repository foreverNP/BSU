package restaurant.controllers.admin;

import java.io.Writer;

import restaurant.services.ClientService;
import restaurant.controllers.IController;
import restaurant.exceptions.ClientServiceException;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CreateClientController implements IController {

    private static final Logger logger = LogManager.getLogger(CreateClientController.class);

    private final ClientService clientService;

    public CreateClientController() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurantPU");
        this.clientService = new ClientService(emf);
    }

    @Override
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer,
            final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String name = request.getParameter("name");
            String balanceStr = request.getParameter("balance");

            try {
                double balance = Double.parseDouble(balanceStr);
                boolean success = clientService.addClient(name, balance);

                if (success) {
                    ctx.setVariable("message", "Client successfully created.");
                } else {
                    ctx.setVariable("errorMessage", "Failed to create client.");
                }
            } catch (NumberFormatException e) {
                logger.error("Invalid balance format", e);
                ctx.setVariable("errorMessage", "Invalid balance format.");
            } catch (ClientServiceException e) {
                logger.error("Failed to add client", e);
                ctx.setVariable("errorMessage", "Error adding client.");
            }
        }

        templateEngine.process("admin/create_client", ctx, writer);
    }
}