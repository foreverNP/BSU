package restaurant.controllers.admin;

import java.io.Writer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebRequest;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import restaurant.controllers.IController;
import restaurant.services.MenuService;
import restaurant.exceptions.MenuServiceException;

public class CreateMenuItemController implements IController {

    private static final Logger logger = LogManager.getLogger(CreateMenuItemController.class);

    private final MenuService menuService;

    public CreateMenuItemController() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurantPU");
        this.menuService = new MenuService(emf);
    }

    @Override
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer,
            final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String name = request.getParameter("name");
            String priceStr = request.getParameter("price");
            String category = request.getParameter("category");

            try {
                double price = Double.parseDouble(priceStr);
                boolean success = menuService.addMenuItem(name, price, category);

                if (success) {
                    ctx.setVariable("message", "Menu item successfully created.");
                } else {
                    ctx.setVariable("errorMessage", "Failed to create menu item.");
                }
            } catch (NumberFormatException e) {
                logger.error("Invalid price format", e);
                ctx.setVariable("errorMessage", "Invalid price format.");
            } catch (MenuServiceException e) {
                logger.error("Failed to add menu item", e);
                ctx.setVariable("errorMessage", "Error adding menu item.");
            }
        }

        templateEngine.process("admin/create_menu_item", ctx, writer);
    }
}
