package gr.aueb.cf.springapp.dto;

import gr.aueb.cf.springapp.entity.Employer;
import gr.aueb.cf.springapp.entity.Project;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


public class EmployeeDTO {

    private Long id;

    @NotNull(message = "Firstname cannot be null")
    @Size(min = 3,message = "Firstname cannot be less than 3 characters")
    private String firstname;

    @NotNull(message = "Lastname cannot be null")
    @Size(min = 3,message = "Lastname cannot be less than 3 characters")
    private String lastname;

    @NotNull(message = "Job title cannot be empty")
    @Size(min = 5, message = "Job Title cannot be less than five characters")
    private String jobTitle;
    @NotNull(message = "Field is required")
    @Min(value = 0, message = "Field must be a positive number")
    private Double salary;
    private Employer employer;

    private List<Project> projects;

    public EmployeeDTO() {
    }

    public EmployeeDTO(Long id, String firstname, String lastname, String jobTitle, Double salary, Employer employer, List<Project> projects) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.employer = employer;
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "Employee with  " +
                ", firstname: '" + firstname + '\'' +
                ", lastname: '" + lastname + '\'';
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

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
