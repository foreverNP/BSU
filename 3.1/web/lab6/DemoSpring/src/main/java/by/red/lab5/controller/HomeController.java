package by.red.lab5.controller;

import java.io.Writer;
import java.util.Calendar;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;



@Controller
public class HomeController  {

	@GetMapping(value={"/home", "/"}
			) 
	 public String getStudents(Model model) {
		 model.addAttribute("today", Calendar.getInstance());
	       return "home";
	       
	    }
	
	
	

}
