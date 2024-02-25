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
import restaurant.entity.UserRole;
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
            String roleParam = request.getParameter("role");

            if (roleParam == null || roleParam.isEmpty()) {
                ctx.setVariable("errorMessage", "Please select a role.");
                templateEngine.process("login", ctx, writer);
                return;
            }

            UserRole role;
            try {
                role = UserRole.valueOf(roleParam.toUpperCase());
            } catch (IllegalArgumentException e) {
                ctx.setVariable("errorMessage", "Unknown role selected.");
                templateEngine.process("login", ctx, writer);
                return;
            }

            switch (role) {
                case ADMIN:
                    String adminLogin = request.getParameter("adminLogin");
                    String adminPassword = request.getParameter("adminPassword");
                    if ("admin".equals(adminLogin) && "admin".equals(adminPassword)) {
                        session.setAttributeValue("role", UserRole.ADMIN);
                        response.sendRedirect("/app/home");
                        return;
                    } else {
                        ctx.setVariable("errorMessage", "Invalid admin credentials.");
                    }
                    break;

                case USER:
                    String clientIdStr = request.getParameter("clientId");
                    try {
                        int clientId = Integer.parseInt(clientIdStr);
                        Client client = clientService.getClientById(clientId);
                        if (client != null) {
                            session.setAttributeValue("role", UserRole.USER);
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

                case GUEST:
                    session.setAttributeValue("role", UserRole.GUEST);
                    response.sendRedirect("/app/home");
                    return;
            }
        }

        templateEngine.process("login", ctx, writer);
    }
}
