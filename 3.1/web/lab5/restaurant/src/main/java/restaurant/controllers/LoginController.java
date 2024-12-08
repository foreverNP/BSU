package restaurant.controllers;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebSession;

import restaurant.entity.Client;
import restaurant.exceptions.ClientServiceException;
import restaurant.services.ClientService;

import java.io.Writer;

public class LoginController implements IController {
    private static final Logger logger = LogManager.getLogger(LoginController.class);

    private final ClientService clientService;

    public LoginController() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurantPU");
        this.clientService = new ClientService(emf);
    }

    @Override
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        IWebSession session = webExchange.getSession();

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String role = request.getParameter("role");

            if (role == null || role.isEmpty()) {
                ctx.setVariable("errorMessage", "Please select a role.");
                templateEngine.process("login", ctx, writer);
                return;
            }

            switch (role) {
                case "admin":
                    String adminLogin = request.getParameter("adminLogin");
                    String adminPassword = request.getParameter("adminPassword");
                    if ("admin".equals(adminLogin) && "admin".equals(adminPassword)) {
                        session.setAttributeValue("role", "admin");
                        // Перенаправим на home для админа
                        response.sendRedirect("/app/home");
                        return;
                    } else {
                        ctx.setVariable("errorMessage", "Invalid admin credentials.");
                    }
                    break;

                case "client":
                    String clientIdStr = request.getParameter("clientId");
                    try {
                        int clientId = Integer.parseInt(clientIdStr);
                        Client client = clientService.getClientById(clientId);
                        if (client != null) {
                            session.setAttributeValue("role", "client");
                            session.setAttributeValue("clientId", clientId);
                            session.setAttributeValue("clientName", client.getName());
                            response.sendRedirect("/app/home");
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
                    break;

                case "guest":
                    // Гость не вводит никаких данных
                    session.setAttributeValue("role", "guest");
                    response.sendRedirect("/app/home");
                    return;

                default:
                    ctx.setVariable("errorMessage", "Unknown role selected.");
            }
        }

        templateEngine.process("login", ctx, writer);
    }
}
