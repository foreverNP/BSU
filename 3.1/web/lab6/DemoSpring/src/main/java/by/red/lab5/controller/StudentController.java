package by.red.lab5.controller;


import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import by.red.lab5.entities.Student;
import by.red.lab5.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;




@Controller

@CrossOrigin(origins = "*")
public class StudentController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	@Autowired
	private  StudentRepository studentRepository;
	
	
	 

	 @GetMapping(value="/StudentsAll"
			) 
	 public String getStudents(Model model) {
		 model.addAttribute("students", studentRepository.findAll());
		 
	       return "student/list";
	       
	    }
	 
	 
	
}
