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

public class StudentController implements IController {
	@Override
	public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {
		WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
		ctx.setVariable("today", Calendar.getInstance());
		StudentsDAO studentsDAO = StudentsDAO.getInstance();

		List<Student> students = studentsDAO.loadStudentsInCourse("Geometry");
		ctx.setVariable("students", students);

		templateEngine.process("student/list", ctx, writer);

	}
}
