package restaurant.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import org.thymeleaf.web.servlet.IServletWebExchange;
import restaurant.controllers.*;
import restaurant.utils.TemplateEngineUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(urlPatterns = "/app/*", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(DispatcherServlet.class);

    private JakartaServletWebApplication application;
    private ITemplateEngine templateEngine;

    @Override
    public void init() {
        this.application = JakartaServletWebApplication.buildApplication(getServletContext());
        this.templateEngine = TemplateEngineUtil.buildTemplateEngine(this.application);

        logger.info("DispatcherServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            final Writer writer = response.getWriter();

            // Работа с cookie для количества посещений
            int visitCount = 0;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("visitCount".equals(cookie.getName())) {
                        try {
                            visitCount = Integer.parseInt(cookie.getValue());
                        } catch (NumberFormatException e) {
                            visitCount = 0;
                        }
                        break;
                    }
                }
            }
            visitCount++;
            Cookie visitCountCookie = new Cookie("visitCount", String.valueOf(visitCount));
            visitCountCookie.setMaxAge(60 * 60 * 24 * 365); // 1 год
            response.addCookie(visitCountCookie);

            // Работа с cookie для времени последнего посещения
            String lastVisit = "Never";
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("lastVisit".equals(cookie.getName())) {
                        long lastVisitTime = Long.parseLong(cookie.getValue());
                        Date lastDate = new Date(lastVisitTime);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        lastVisit = formatter.format(lastDate);
                        break;
                    }
                }
            }

            // Устанавливаем новое время последнего посещения
            long currentTime = System.currentTimeMillis();
            Cookie lastVisitCookie = new Cookie("lastVisit", String.valueOf(currentTime));
            lastVisitCookie.setMaxAge(60 * 60 * 24 * 365); // 1 год
            response.addCookie(lastVisitCookie);

            // Передача данных в Thymeleaf
            IServletWebExchange webExchange = this.application.buildExchange(request, response);
            String path = request.getPathInfo();
            logger.info("Received request for path: {}", path);

            IController controller = ControllerMappings.resolveControllerForRequest(path);
            if (controller == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            request.setAttribute("visitCount", visitCount);
            request.setAttribute("lastVisit", lastVisit);

            logger.debug("visitCount: {}", visitCount);
            logger.debug("lastVisit: {}", lastVisit);

            controller.process(webExchange, templateEngine, writer);

        } catch (Exception e) {
            logger.error("Error processing request", e);
            throw new ServletException(e);
        }
    }
}
