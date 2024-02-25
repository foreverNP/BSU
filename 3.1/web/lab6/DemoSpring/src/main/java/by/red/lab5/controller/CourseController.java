package by.red.lab5.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.ui.Model;
import by.red.lab5.entities.Course;

import by.red.lab5.repository.CourseRepository;


@RestController
@CrossOrigin(origins = "*")
public class CourseController {

	@Autowired
	private CourseRepository courseRepository;
	
	
	 @GetMapping(value="/Courses", produces = { MediaType.APPLICATION_JSON_VALUE })
	    public List<Course> getCourses() {
	        return (List<Course>) courseRepository.findAll();
	    }
	 
	 @GetMapping(value="/CoursesAll"
			// , produces = { MediaType.APPLICATION_JSON_VALUE }
	        )
	    public String getCourses1(Model model) {
		 model.addAttribute("courses", courseRepository.findAll());
	        return "/courses/list";
	    } 
	 
	
	 @PostMapping(value="/course",
			 
			 consumes = {"application/xml","application/json"},
			 produces = MediaType.APPLICATION_JSON_VALUE)
	    void addCourse( @RequestBody Course course) {
		 courseRepository.save(course);
	    }
	
	
}
