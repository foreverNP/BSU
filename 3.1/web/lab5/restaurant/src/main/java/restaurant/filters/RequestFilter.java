package restaurant.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import org.thymeleaf.web.servlet.IServletWebExchange;

import restaurant.controllers.IController;

@WebFilter("/app/*")
public class RequestFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(RequestFilter.class);

    private JakartaServletWebApplication application;
    private ITemplateEngine templateEngine;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("RequestFilter initialized.");

        this.application = JakartaServletWebApplication.buildApplication(filterConfig.getServletContext());

        final WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);

        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));
        templateResolver.setCacheable(true);

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        this.templateEngine = templateEngine;
    }

    @Override
    public void doFilter(jakarta.servlet.ServletRequest servletRequest, jakarta.servlet.ServletResponse servletResponse,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getPathInfo();
        if (path == null) {
            path = request.getServletPath();
        }

        IController controller = ControllerMappings.resolveControllerForRequest(path);
        if (controller == null) {
            System.out.println("No controller found for path: " + path);
            chain.doFilter(request, response);
            return;
        }

        int visitCount = 0;
        String lastVisit = "Never";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("visitCount".equals(cookie.getName())) {
                    try {
                        visitCount = Integer.parseInt(cookie.getValue());
                    } catch (NumberFormatException e) {
                        visitCount = 0;
                    }
                } else if ("lastVisit".equals(cookie.getName())) {
                    long lastVisitTime = Long.parseLong(cookie.getValue());
                    Date lastDate = new Date(lastVisitTime);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    lastVisit = formatter.format(lastDate);
                }
            }
        }
        visitCount++;
        Cookie visitCountCookie = new Cookie("visitCount", String.valueOf(visitCount));
        visitCountCookie.setMaxAge(60 * 60 * 24 * 365); // 1 год
        response.addCookie(visitCountCookie);

        long currentTime = System.currentTimeMillis();
        Cookie lastVisitCookie = new Cookie("lastVisit", String.valueOf(currentTime));
        lastVisitCookie.setMaxAge(60 * 60 * 24 * 365); // 1 год
        response.addCookie(lastVisitCookie);

        // Передача управления контроллеру
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");

            IServletWebExchange webExchange = this.application.buildExchange(request, response);
            Writer writer = response.getWriter();

            // Передадим данные о визитах в request атрибуты
            request.setAttribute("visitCount", visitCount);
            request.setAttribute("lastVisit", lastVisit);

            logger.debug("Received request for path: {}", path);
            logger.debug("visitCount: {}", visitCount);
            logger.debug("lastVisit: {}", lastVisit);

            controller.process(webExchange, templateEngine, writer, request, response);

        } catch (Exception e) {
            logger.error("Error processing request", e);
            throw new ServletException(e);
        }
    }

    @Override
    public void destroy() {
        logger.info("RequestFilter destroyed.");
    }
}
