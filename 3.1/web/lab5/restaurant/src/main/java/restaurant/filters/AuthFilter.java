package restaurant.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import restaurant.entity.UserRole;

@WebFilter("/app/*")
public class AuthFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(AuthFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthFilter initialized.");
    }

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpResp = (HttpServletResponse) response;
        HttpSession session = httpReq.getSession(false);

        String path = httpReq.getRequestURI();
        logger.debug("AuthFilter checking path: {}", path);

        // Ресурсы без авторизации:
        boolean isLoginPage = path.startsWith("/app/login");
        boolean isStatic = path.startsWith("/styles") || path.startsWith("/images") || path.startsWith("/favicon.ico");
        boolean isGuestAllowedPage = path.equals("/app/client/menu");

        if (isLoginPage || isStatic || isGuestAllowedPage) {
            chain.doFilter(request, response);
            return;
        }

        UserRole role = (session != null) ? (UserRole) session.getAttribute("role") : null;

        // Если нет сессии или роли, перенаправить на логин
        if (role == null) {
            logger.debug("No role found, redirecting to login.");
            httpResp.sendRedirect("/app/login");
            return;
        }

        // Проверка прав доступа
        if (path.startsWith("/app/admin")) {
            // Только администратор
            if (role != UserRole.ADMIN) {
                logger.warn("Access denied. Role {} tried to access admin resource.", role);
                httpResp.sendRedirect("/app/login");
                return;
            }
        } else if (path.startsWith("/app/client")) {
            // Доступ для USER или ADMIN
            if (role != UserRole.ADMIN && role != UserRole.USER) {
                logger.warn("Access denied. Role {} tried to access client resource.", role);
                httpResp.sendRedirect("/app/login");
                return;
            }
        }

        // Если все проверки пройдены, пропускаем дальше
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("AuthFilter destroyed.");
    }
}
