package by.red.lab5.repository;

import org.springframework.stereotype.Repository;

import by.red.lab5.entities.Student;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


@Repository
public interface StudentRepository extends CrudRepository<Student, Long>{

	@Query(value = "SELECT s FROM Student s ORDER BY id")
	Page<Student> findAllStudentsWithPagination(Pageable pageable);

	@Query(value = "SELECT s FROM Student s")
	List<Student> findAllStudents(Sort sort);
	
	@Query(value =  "select st " +
			  "from Student st, " +
    		  "in (st.courses) c " +
    		  "where c.name = :courseName")
	public List<Student> findStudentsInCourse (@Param("courseName") String courseName); 
		
	
}
