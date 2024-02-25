package restaurant.controllers.client;

import restaurant.services.MenuService;
import restaurant.controllers.IController;
import restaurant.entity.Menu;
import restaurant.exceptions.MenuServiceException;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.Writer;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MenuController implements IController {

    private static final Logger logger = LogManager.getLogger(MenuController.class);

    private final MenuService menuService;

    public MenuController() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurantPU");
        this.menuService = new MenuService(emf);
    }

    @Override
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer,
            final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());

        try {
            Menu menu = menuService.getMenu();
            ctx.setVariable("menu", menu);

            templateEngine.process("client/menu", ctx, writer);
        } catch (MenuServiceException e) {
            logger.error("Failed to retrieve menu", e);
            ctx.setVariable("errorMessage", "Cannot retrieve menu.");
            templateEngine.process("error", ctx, writer);
        }
    }
}
