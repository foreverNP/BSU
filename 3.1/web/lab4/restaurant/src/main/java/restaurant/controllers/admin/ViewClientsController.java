package restaurant.controllers.admin;

import restaurant.services.ClientService;
import restaurant.controllers.IController;
import restaurant.entity.Client;
import restaurant.exceptions.ClientServiceException;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import java.io.Writer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class ViewClientsController implements IController {

    private static final Logger logger = LogManager.getLogger(ViewClientsController.class);

    private final ClientService clientService;

    public ViewClientsController() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurantPU");
        this.clientService = new ClientService(emf);
    }

    @Override
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer)
            throws Exception {
        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());

        try {
            List<Client> clients = clientService.getAllClients();
            ctx.setVariable("clients", clients);
            templateEngine.process("admin/view_clients", ctx, writer);
        } catch (ClientServiceException e) {
            logger.error("Failed to retrieve clients", e);
            ctx.setVariable("errorMessage", "Cannot retrieve clients.");
            templateEngine.process("error", ctx, writer);
        }
    }
}
