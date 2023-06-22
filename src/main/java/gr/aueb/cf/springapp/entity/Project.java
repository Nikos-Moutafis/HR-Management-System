package gr.aueb.cf.springapp.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "PROJECTS")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "START_DATE")
    private String startDate;

    @Column(name = "END_DATE")
    private String endDate;

    @Column(name = "STATUS")
    private String status;

    @ManyToOne(cascade = CascadeType.REMOVE, optional = false)
    @JoinColumn(name = "employer_id", nullable = false)
    @NotNull
    private Employer employer;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PROJECTS_EMPLOYEES", joinColumns = @JoinColumn(name = "PROJECT_ID", referencedColumnName = "ID"),
                inverseJoinColumns = @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID"),
    uniqueConstraints = @UniqueConstraint(columnNames = {"PROJECT_ID", "EMPLOYEE_ID"}))
    private List<Employee> employees = new ArrayList<>();

    public Project() {
    }

    public Project(Long id, String name, String description, String startDate,
                   String endDate, String status, Employer employer, List<Employee> employees) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.employer = employer;
        this.employees = employees;
    }

    /**
     * Deletes an employee from the project's list of employees.
     * Also ensures that the project is removed from the employee's list of project.
     * @param employee The employee to be deleted.
     */
    public void  deleteEmployee(Employee employee){
        boolean found = false;
        this.employees.remove(employee);

        for (Project project : employee.getProjects()){
            if (project == this){
                found = true;
                break;
            }
        }
        if (found) employee.deleteProject(this);
    }

    /**
     * Adds an employee to the project's list of employees.
     * Also ensures that the project is added to the employee's list of projects.
     * @param employee The employee to be added.
     */
    public void addEmployee(Employee employee){
        this.employees.add(employee);
        for (Project project : employee.getProjects()){
            if (project == this){
                return;
            }
        }
        employee.addProject(this);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
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

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (!id.equals(project.id)) return false;
        if (!name.equals(project.name)) return false;
        if (!description.equals(project.description)) return false;
        if (!startDate.equals(project.startDate)) return false;
        if (!endDate.equals(project.endDate)) return false;
        if (!status.equals(project.status)) return false;
        if (!employer.equals(project.employer)) return false;
        return Objects.equals(employees, project.employees);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + startDate.hashCode();
        result = 31 * result + endDate.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + employer.hashCode();
        result = 31 * result + (employees != null ? employees.hashCode() : 0);
        return result;
    }
}
