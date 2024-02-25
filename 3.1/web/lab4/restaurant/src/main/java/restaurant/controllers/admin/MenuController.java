package restaurant.controllers.admin;

import restaurant.services.MenuService;
import restaurant.controllers.IController;
import restaurant.entity.Menu;
import restaurant.exceptions.MenuServiceException;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.Writer;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MenuController implements IController {

    private static final Logger logger = LogManager.getLogger(MenuController.class);

    private final MenuService menuService;

    public MenuController() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("restaurantPU");
        this.menuService = new MenuService(emf);
    }

    @Override
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer)
            throws Exception {
        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());

        try {
            Menu menu = menuService.getMenu();
            ctx.setVariable("menu", menu);

            templateEngine.process("admin/menu", ctx, writer);
        } catch (MenuServiceException e) {
            logger.error("Failed to retrieve menu", e);
            ctx.setVariable("errorMessage", "Cannot retrieve menu.");
            templateEngine.process("error", ctx, writer);
        }
    }
}
