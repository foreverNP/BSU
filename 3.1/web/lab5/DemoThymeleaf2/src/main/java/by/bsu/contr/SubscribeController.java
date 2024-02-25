package by.bsu.contr;

import java.io.Writer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

import by.bsu.dao.StudentsDAO;
import by.bsu.entity.Student;

public class SubscribeController implements IController {
	@Override
	public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {
		 WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
	        ctx.setVariable("today", Calendar.getInstance());
	
	        
	        Student student = new Student();
	        student.setFirstName(webExchange.getRequest().getParameterValue("firstName"));
	        student.setLastName(webExchange.getRequest().getParameterValue("lastName"));
	        webExchange.getSession().setAttributeValue("student", student); 		  
	        templateEngine.process("subscribe", ctx, writer);
		
	}
}
