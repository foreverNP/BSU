package by.bsu.contr;

import java.io.Writer;
import java.util.Calendar;
import java.util.Locale;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

public class LocaleController implements IController {
	@Override
	public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {
		 WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
	    
	        
	        String lang =  webExchange.getRequest().getParameterValue("lang");
	        
	         
			if (lang != null) {
				 
				Locale locale = new Locale.Builder()
						.setLanguage(lang)
						.setRegion(lang)
						.build();
				 Locale.setDefault(locale);
				
				webExchange.getSession().setAttributeValue("language", locale.getLanguage());
				
			
			  
	        
	}
}
}
