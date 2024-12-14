package by.red.lab5.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import by.red.lab5.entities.Course;
import by.red.lab5.entities.Student;

@Repository
public interface CourseRepository extends CrudRepository <Course, Long> {

	@Query(value = "SELECT c FROM Course c")
	List< Course> findAllCourses(Sort sort);
}
