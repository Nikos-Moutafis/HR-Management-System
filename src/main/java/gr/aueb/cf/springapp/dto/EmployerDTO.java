package gr.aueb.cf.springapp.dto;

import gr.aueb.cf.springapp.entity.Employee;
import gr.aueb.cf.springapp.entity.Project;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class EmployerDTO {


    private Long id;
    @NotNull(message = "Name cannot be null")
    @Size(min = 5, message = "Name cannot be less than five characters")
    private String name;
    @NotNull(message = "Address cannot be null")
    @Size(min = 3, message = "Address cannot be less than three characters")
    private String address;
    private List<Employee> employees;

    private List<Project> projects;

    public EmployerDTO() {
    }

    public EmployerDTO(Long id, String name, String address, List<Employee> employees, List<Project> projects) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.employees = employees;
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "EmployerDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
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

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
