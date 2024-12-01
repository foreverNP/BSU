package by.bsu.filter;

import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;


import by.bsu.contr.IController;
import by.bsu.dao.StudentsDAO;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = "/*")
public class WelcomeFilter  implements Filter{
	 private JakartaServletWebApplication application;
	 private ITemplateEngine templateEngine;
	
	 @Override
	 public void init(FilterConfig filterConfig){
		 
		 this.application =
	        JakartaServletWebApplication.buildApplication(filterConfig.getServletContext());
		
		 this.templateEngine = buildTemplateEngine(this.application);

		 StudentsDAO studentsDAO = StudentsDAO.getInstance();
	
	 }
	 
	 
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!process((HttpServletRequest)request, (HttpServletResponse)response)) {
            chain.doFilter(request, response);
        }
		
	}
	
	private ITemplateEngine buildTemplateEngine(final IWebApplication application) {
		final WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);
	//	 StringTemplateResolver templateResolver = new StringTemplateResolver();
        // HTML is the default mode, but we will set it anyway for better understanding of code
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // This will convert "home" to "/WEB-INF/templates/home.html"
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        // Set template cache TTL to 1 hour. If not set, entries would live in cache until expelled by LRU
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));

        // Cache is set to true by default. Set to false if you want templates to
        // be automatically updated when modified.
        templateResolver.setCacheable(true);
       IMessageResolver stringTemplateResolver = new StandardMessageResolver();
        
        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setMessageResolver(stringTemplateResolver);
        return templateEngine;
	}
	
	 private boolean process(final HttpServletRequest request, final HttpServletResponse response)
	            throws ServletException {
		 try {
				
				request.getSession().setAttribute("calend", 
						Calendar.getInstance());
				
			final IServletWebExchange webExchange = this.application.buildExchange(request, response);
	        final IWebRequest webRequest = webExchange.getRequest();
	        final Writer writer = response.getWriter();
	       
	        if (webRequest.getPathWithinApplication().startsWith("/css") ||
                    webRequest.getPathWithinApplication().startsWith("/images") ||
                    webRequest.getPathWithinApplication().startsWith("/favicon")) {
                return false;
            }
	        
	        
	         IController controller = ControllerMappings.resolveControllerForRequest(webRequest);
	        if (controller == null) {
	    //    	controller=new HomeController();
	        	return false;
	        }
	        response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=UTF-8");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			
				controller.process(webExchange, templateEngine, writer);
				return true;
		
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
