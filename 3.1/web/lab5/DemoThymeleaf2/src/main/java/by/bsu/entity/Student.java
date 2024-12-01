package by.bsu.entity;


import jakarta.persistence.*;


import java.util.Calendar;
import java.util.Date;
//import java.sql.Date;
import java.util.List;

//import javax.persistence.*;

@Entity
@Table(name = "STUDENT")
// mapping of named query
@NamedQueries({
@NamedQuery(name = "studentByLastName", 
		   query = "select st " +
				  "from Student st " +
		    		  "where st.lastName = :lastName " +
		    		  "and st.startDate > :date"),
@NamedQuery(name = "studentsInCourse", 
		   query = "select st " +
				  "from Student st, " +
		    		  "in (st.courses) c " +
		    		  "where c.name = :courseName" )
})
public class Student {
//	private static final TemporalType DATE = null;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false, unique = true)
	private int id;
	
	@Column(name = "FIRST_NAME")
	private String firstName;
	
	@Column(name = "LAST_NAME")
	private String lastName;

	@Basic
    @Temporal(TemporalType.DATE)
    private Date startDate;

	
	// mapping many-to-many relationship at the owner side
	@ManyToMany(
		cascade = {CascadeType.PERSIST, CascadeType.MERGE}
	)
	// specifying join table
	@JoinTable(
		name = "COURSE_STUDENT",
		joinColumns = {@JoinColumn(name = "STUDENT_ID")},
		inverseJoinColumns={@JoinColumn(name="COURSE_ID")}
	)
	private List<Course> courses;

	public void setFirstName(String firstName) {
		this.firstName=firstName;
		
	}

	public void setLastName(String lastName) {
		this.lastName=lastName;
		
	}

	public void setCourses(List<Course> courses2) {
		this.courses=courses2;
		
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getFirstName() {
		
		return firstName;
	}

	public String getLastName() {
		
		return lastName;
	}

	public int getId() {
		
		return id;
	}

	public List<Course>  getCourses() {


		return courses;
	}
}

