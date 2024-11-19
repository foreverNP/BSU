package restaurant.controllers.client;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.IWebSession;

import restaurant.controllers.IController;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import restaurant.services.ClientService;
import restaurant.entity.Client;
import restaurant.exceptions.ClientServiceException;

import java.io.Writer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginController implements IController {

    private static final Logger logger = LogManager.getLogger(LoginController.class);

    private final ClientService clientService;

    public LoginController() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurantPU");
        this.clientService = new ClientService(emf);
    }

    @Override
    public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {

        IWebRequest request = webExchange.getRequest();

        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String clientIdStr = request.getParameterValue("clientId");

            try {
                int clientId = Integer.parseInt(clientIdStr);
                Client client = clientService.getClientById(clientId);

                if (client != null) {
                    IWebSession session = webExchange.getSession();
                    session.setAttributeValue("clientId", clientId);
                    session.setAttributeValue("clientName", client.getName());
                    ctx.setVariable("message", "Client successfully logged in. Welcome, " + client.getName() + "!");
                    templateEngine.process("client/login", ctx, writer);
                    return;
                } else {
                    ctx.setVariable("errorMessage", "Client not found.");
                }
            } catch (NumberFormatException e) {
                logger.error("Invalid client ID format", e);
                ctx.setVariable("errorMessage", "Invalid client ID format.");
            } catch (ClientServiceException e) {
                logger.error("Error retrieving client", e);
                ctx.setVariable("errorMessage", "Error retrieving client data.");
            }
        }

        templateEngine.process("client/login", ctx, writer);
    }
}
