package by.bsu.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;

import by.bsu.contr.HomeController;
import by.bsu.contr.IController;
import by.bsu.dao.StudentsDAO;

import org.eclipse.persistence.jpa.PersistenceProvider;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/welcome/*", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JakartaServletWebApplication application;
	private ITemplateEngine templateEngine;

	@Override
	public void init() {
		this.application = JakartaServletWebApplication.buildApplication(getServletContext());

		this.templateEngine = buildTemplateEngine(this.application);

		StudentsDAO studentsDAO = StudentsDAO.getInstance();

		studentsDAO.createDemoStudent();

	}

	private ITemplateEngine buildTemplateEngine(final IWebApplication application) {
		final WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);

		// HTML is the default mode, but we will set it anyway for better understanding
		// of code
		templateResolver.setTemplateMode(TemplateMode.HTML);
		// This will convert "home" to "/WEB-INF/templates/home.html"
		templateResolver.setPrefix("/WEB-INF/templates/");
		templateResolver.setSuffix(".html");
		// Set template cache TTL to 1 hour. If not set, entries would live in cache
		// until expelled by LRU
		templateResolver.setCacheTTLMs(Long.valueOf(3600000L));

		// Cache is set to true by default. Set to false if you want templates to
		// be automatically updated when modified.
		templateResolver.setCacheable(true);

		final TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);

		return templateEngine;
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {

			request.getSession().setAttribute("calend",
					Calendar.getInstance());

			final IServletWebExchange webExchange = this.application.buildExchange(request, response);
			final IWebRequest webRequest = webExchange.getRequest();
			final Writer writer = response.getWriter();

			IController controller = ControllerMappings.resolveControllerForRequest(webRequest);
			if (controller == null) {
				controller = new HomeController();
			}

			response.setContentType("text/html;charset=UTF-8");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);

			controller.process(webExchange, templateEngine, writer);

		} catch (Exception e) {
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} catch (final IOException ignored) {
				// Just ignore this
			}
			throw new ServletException(e);
		}
	}

}
