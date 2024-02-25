package restaurant.controllers;

import java.io.Writer;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import restaurant.entity.UserRole;

public class HomeController implements IController {
    @Override
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        UserRole role = (UserRole) webExchange.getSession().getAttributeValue("role");

        if (role == UserRole.ADMIN) {
            templateEngine.process("admin/home_admin", ctx, writer);
        } else if (role == UserRole.USER) {
            ctx.setVariable("clientName", webExchange.getSession().getAttributeValue("clientName"));
            templateEngine.process("client/home_client", ctx, writer);
        } else if (role == UserRole.GUEST) {
            templateEngine.process("guest/home_guest", ctx, writer);
        } else {
            // If role is missing, redirect to login
            response.sendRedirect("/app/login");
        }
    }
}