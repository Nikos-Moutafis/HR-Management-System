package gr.aueb.cf.springapp.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="EMPLOYEES")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;

	@Column(name = "FIRSTNAME")
	private String firstname;

	@Column(name = "LASTNAME")
	private String lastname;

	@Column(name = "JOBTITLE")
	private String jobTitle;

	@Column(name = "SALARY")
	private Double salary;


	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "EMPLOYER_ID", referencedColumnName = "ID")
	private Employer employer;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "EMPLOYEES_PROJECTS", joinColumns = @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID"),
			inverseJoinColumns = @JoinColumn(name = "PROJECT_ID", referencedColumnName = "ID"),
			uniqueConstraints = @UniqueConstraint(columnNames = {"EMPLOYEE_ID", "PROJECT_ID"}))
	private List<Project> projects = new ArrayList<>();

	public Employee(Long id, String firstname, String lastname, String jobTitle,
					Double salary, Employer employer,
					List<Project> projects) {
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.jobTitle = jobTitle;
		this.salary = salary;
		this.employer = employer;
		this.projects = projects;
	}

	public Employee() {
	}

	/**
	 * Adds a project to the employee's list of projects.
	 * Also ensures that the employee is added to the project's list of employees.
	 * @param project The project to be added.
	 */
	public void addProject(Project project) {
		this.projects.add(project);
		for (Employee employee : project.getEmployees()) {
			if (employee == this) {
				return;
			}
		}
		project.addEmployee(this);
	}

	/**
	 * Deletes a project from the employee's list of projects.
	 * Also ensures that the employee is removed from the project's list of employees.
	 * @param project The project to be deleted.
	 */
	public void deleteProject(Project project) {
		boolean found = false;
		this.projects.remove(project);
		for (Employee employee : project.getEmployees()) {
			if (employee == this) {
				found = true;
				break;
			}
		}
		if (found) project.deleteEmployee(this);
	}

	/**
	 * Adds an employer to the employee.
	 * @param employer The employer to be added.
	 */
	public void addEmployer(Employer employer) {
		this.setEmployer(employer);
		employer.getEmployees().add(this);
	}

	/**
	 * Removes the employer from the employee.
	 */
	public void removeEmployer() {
		if (this.getEmployer() != null) {
			this.getEmployer().getEmployees().remove(this);
			this.setEmployer(null);
		}
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

	public Employer getEmployer() {
		return employer;
	}

	public void setEmployer(Employer employer) {
		this.employer = employer;
	}

	public List<Project> getAllProjects() {
		List<Project> projectList = getProjects();
		if (projectList == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(getProjects());
	}
	protected List<Project> getProjects() {
		return projects;
	}
	protected void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Employee employee = (Employee) o;

		if (!id.equals(employee.id)) return false;
		if (!firstname.equals(employee.firstname)) return false;
		if (!lastname.equals(employee.lastname)) return false;
		if (!jobTitle.equals(employee.jobTitle)) return false;
		if (!salary.equals(employee.salary)) return false;
		if (!Objects.equals(employer, employee.employer)) return false;
		return Objects.equals(projects, employee.projects);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + firstname.hashCode();
		result = 31 * result + lastname.hashCode();
		result = 31 * result + jobTitle.hashCode();
		result = 31 * result + salary.hashCode();
		result = 31 * result + (employer != null ? employer.hashCode() : 0);
		result = 31 * result + (projects != null ? projects.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", firstName=" + firstname + ", lastName=" + lastname
				+ ", employer=" + employer.getName() + "]";
	}
}