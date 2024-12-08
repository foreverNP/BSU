package by.bsu.filter;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import by.bsu.contr.IController;
import by.bsu.contr.LocaleController;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter(filterName="LocaleFilter", urlPatterns = "/*")
public class LocaleFilter implements Filter {
	private JakartaServletWebApplication application;
	 private ITemplateEngine templateEngine;
	
	 public void init(FilterConfig fConfig) throws ServletException {
		 if ( this.application==null) {
			 this.application= JakartaServletWebApplication.buildApplication(fConfig.getServletContext());
		 }
		
	 } 
	 
	 public void destroy() {	}

	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		final IServletWebExchange webExchange = this.application.buildExchange(( HttpServletRequest)request, ( HttpServletResponse)response);
        final IWebRequest webRequest = webExchange.getRequest();
        response.setContentType("text/html;charset=UTF8");
		response.setCharacterEncoding("UTF8");
        final Writer writer = response.getWriter();
       
        IController controller =new LocaleController();
        
        		try {
					controller.process(webExchange, templateEngine, writer);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
		
		
				
		chain.doFilter(request, response);
	}
}


	
	


