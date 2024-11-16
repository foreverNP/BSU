package by.bsu.entity;

import java.util.List;


import jakarta.persistence.*;
@Entity
@Table(name = "COURSE")
public class Course {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "LECTOR")
	private String lector;

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

