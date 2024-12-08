package restaurant.controllers;

import java.io.Writer;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HomeController implements IController {
    @Override
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        String role = (String) webExchange.getSession().getAttributeValue("role");

        if ("admin".equals(role)) {
            templateEngine.process("admin/home_admin", ctx, writer);
        } else if ("client".equals(role)) {
            ctx.setVariable("clientName", webExchange.getSession().getAttributeValue("clientName"));
            templateEngine.process("client/home_client", ctx, writer);
        } else if ("guest".equals(role)) {
            templateEngine.process("guest/home_guest", ctx, writer);
        } else {
            // Если по какой-то причине роли нет, редиректим назад на login
            response.sendRedirect("/app/login");
        }
    }
}
