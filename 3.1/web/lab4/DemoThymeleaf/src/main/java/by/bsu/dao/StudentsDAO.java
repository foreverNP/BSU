package by.bsu.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
//import java.sql.Date;
import java.util.Date;
import java.util.List;

import org.eclipse.persistence.jpa.PersistenceProvider;

import by.bsu.entity.Course;
import by.bsu.entity.Student;
import jakarta.persistence.*;


public class StudentsDAO {
	private static final String ENTITY_MANAGER_FACTORY_NAME = 
			"simpleFactory2";
private EntityManagerFactory factory;

private static StudentsDAO instance=null;
public static StudentsDAO getInstance() {
	if (instance == null) {
		System.out.println(
		"Creating Singleton");
		instance = new StudentsDAO( );
		}
		return instance;

}
	public StudentsDAO() {
	
		factory= new PersistenceProvider().createEntityManagerFactory("simpleFactory2", null);
	}
	
	public StudentsDAO(EntityManagerFactory factory) {
		this.factory = factory;;	
		System.out.println("factory =" + factory); 
	}
	

	public void createDemoStudent() {
		EntityManager entityManager = null;
		EntityTransaction transaction = null;
		try {
			entityManager = factory.createEntityManager();
			transaction = entityManager.getTransaction();
			transaction.begin();
			
			// create student
			Student student = new Student();
			student.setFirstName("Sergey");
			student.setLastName("Sidorov");
			student.setStartDate((java.util.Date) Calendar.getInstance().getTime());
			// create course
			Course course = new Course();
			course.setLector("Ivanov");
			course.setName("Geometry");
			
			List<Student> students = new ArrayList<Student>();
			List<Course> courses = new ArrayList<Course>();
			// associate student with course
			students.add(student);
			courses.add(course);
			course.setStudents(students);
			student.setCourses(courses);
			
			// save student to database
			entityManager.persist(student);
			transaction.commit();
			
		} catch(Exception e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			if (entityManager != null && entityManager.isOpen()) {
			//	entityManager.close();
			}
		}
	}

	/**
	 * Demonstrates named query.
	 * Display student list with the specified last name 
	 * @param lastName the last name of students to be displayed
	 */
	@SuppressWarnings("unchecked")
	public List<Student> loadStudentsByLastName(String lastName, Date d) {
		EntityManager entityManager = null;
		try{
		 entityManager = factory.createEntityManager();
		// create named query
		Query query = entityManager.createNamedQuery("studentByLastName");
		// set parameter lastName
		query.setParameter("lastName", lastName);
		query.setParameter("date", d);
		// execute query
		List<Student> studentList = (List<Student>)query.getResultList();
		return studentList;
		}
	   finally{
		  entityManager.close();
	   }
	}
		public List<Student> loadStudentsInCourse (String courseName) {
			EntityManager entityManager=null;
			try{
			 entityManager = factory.createEntityManager();
			// create named query
			Query query = entityManager.createNamedQuery("studentsInCourse");
			// set parameter lastName
		   	query.setParameter("courseName", courseName);
			// execute query
			List<Student> studentList = (List<Student>)query.getResultList();
			
			
			return studentList;
			}
			finally{  
				entityManager.close();
			}
			}

	}
