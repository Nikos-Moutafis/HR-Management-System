package gr.aueb.cf.springapp.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "EMPLOYERS")
public class Employer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ADDRESS")
    private String address;

    @OneToMany( mappedBy = "employer")
    private List<Employee> employees = new ArrayList<>();
    @OneToMany(mappedBy = "employer",orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Project> projects = new ArrayList<>();

    public Employer() {
    }

    public Employer(Long id, String name, String address, List<Employee> employees, List<Project> projects) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.employees = employees;
        this.projects = projects;
    }


    /**
     * Adds a project to the employer's list of projects.
     * @param project The project to be added.
     */
    public void addProject(Project project){
        this.projects.add(project);
    }

    /**
     * Deletes a project from the employer's list of projects.
     * @param project The project to be deleted.
     */
    public void deleteProject(Project project){
        this.getProjects().remove(project);
        project.setEmployer(null);
    }

    /**
     * Adds an employee to the employer's list of employees.
     * @param employee The employee to be added.
     */
    public void addEmployee(Employee employee){
        this.getEmployees().add(employee);
        employee.setEmployer(this);
    }

    /**
     * Deletes an employee from the employer's list of employees.
     * @param employee The employee to be deleted.
     */
    public void deleteEmployee(Employee employee){
        this.getEmployees().remove(employee);
        employee.setEmployer(null);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    protected List<Employee> getEmployees() {
        return employees;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employeeList = getEmployees();
        if (employeeList == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(getEmployees());
    }
    protected void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    protected List<Project> getProjects() {
        return projects;
    }

    public List<Project> getAllProjects() {
        List<Project> projectList = getProjects();
        if (projectList == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(getProjects());
    }

    protected void setProjects(List<Project> projects) {
        this.projects = projects;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Employer employer = (Employer) o;

        if (!id.equals(employer.id)) return false;
        if (!name.equals(employer.name)) return false;
        if (!address.equals(employer.address)) return false;
        if (!Objects.equals(employees, employer.employees)) return false;
        return Objects.equals(projects, employer.projects);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + (employees != null ? employees.hashCode() : 0);
        result = 31 * result + (projects != null ? projects.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Employer{" +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

}
