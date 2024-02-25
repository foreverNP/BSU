package by.red.lab5.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
@Entity
@Table(name = "COURSE")
public class Course {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	
	
	
	@Column(name = "LECTOR")
	private String lector;
 
	@Column(name = "NAME")
	private String name;
	
	
	public Course() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Course( String lector, String name) {
		super();
	//	this.id = id;
		
		this.lector = lector;
		this.name = name;
	}

	
	public Course(int id, String lector, String name) {
		super();
		this.id = id;
		this.lector = lector;
		this.name = name;
	}


	@JsonIgnore
	// mapping of many-to-many relationship in the inverse side
	@ManyToMany(
		cascade = {CascadeType.PERSIST, CascadeType.MERGE},
		mappedBy = "courses"
	)
	private List<Student> students;

	public void setLector(String lector) {
		this.lector=lector;
		
	}

	public void setName(String name) {
		this.name=name;
		
	}

	public void setStudents(List<Student> students2) {
		this.students=students2;
		
	}

	public String getName() {
		
		return name;
	}

	public String getLector() {
		
		return lector;
	}

	public List<Student> getStudents() {
		
		return students;
	}
}

